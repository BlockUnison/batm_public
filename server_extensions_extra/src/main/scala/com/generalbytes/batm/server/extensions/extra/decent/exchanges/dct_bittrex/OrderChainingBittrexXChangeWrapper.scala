package com.generalbytes.batm.server.extensions.extra.decent.exchanges.dct_bittrex

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.generalbytes.batm.common.adapters.ExchangeAdapterDecorator
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.common.utils.XChangeUtils._
import com.generalbytes.batm.server.extensions.extra.decent.exchanges.dct_bittrex.BittrexXChangeWrapper.ErrorDecorator
import com.generalbytes.batm.server.extensions.extra.decent.sources.dct_bittrex.FallbackBittrexTicker
import com.generalbytes.batm.server.extensions.extra.decent.utils.BittrexUtils._
import org.knowm.xchange.dto.Order.OrderType
import shapeless._

class OrderChainingBittrexXChangeWrapper[F[_]: ConcurrentEffect](exchange: Exchange[F], midCurrency: Currency)
  extends ExchangeAdapterDecorator[F](exchange) with LoggingSupport {

  private val amountLens = lens[TradeOrder].amount.amount
  private val currencyPairLens = lens[TradeOrder].currencyPair
  private val currencyPairAndAmountLens = currencyPairLens ~ amountLens

  override def fulfillOrder(order: TradeOrder): F[Identifier] = {
    if (order.currencyPair.counter === midCurrency || order.currencyPair.base === midCurrency) exchange.fulfillOrder(order)
    else {
      val midCurrencyAmount = getAmountInMidCurrency(order)

      val result = for {
        amount <- midCurrencyAmount
        fstOrder = createFirstSubOrder(order, amount)
        _ <- logM(fstOrder)
        _ <- exchange.fulfillOrder(fstOrder)
        sndOrderTemp = createSecondSubOrder(order, amount)
        revisedAmount <- getInverseAmountInCurrency(CurrencyPair(midCurrency, order.currencyPair.base), getOrderType(order), amount)   // TODO: refactor this
        _ <- logM(revisedAmount, "RevisedAmount")
        sndOrder = createSecondSubOrder(sndOrderTemp, revisedAmount)
        _ <- logM(sndOrder)
        txId <- exchange.fulfillOrder(sndOrder)
      } yield txId

      result.handleErrorWith {
        case e @ ErrorDecorator(_, CurrencyPair(_, mc)) if mc === midCurrency =>
          // failed on second currency
          val undoTransaction = createUndoOrder(order, midCurrencyAmount)
          undoTransaction.flatMap(_ => raise[F](e))
        case e => raise[F](e)
      }
    }
  }

  private def getInverseAmountInCurrency(currencyPair: CurrencyPair, orderType: OrderType, counterAmount: Amount): F[Amount] =
    getAmountInCurrency(currencyPair, orderType.inverse, counterAmount, _ / _)

  private def getAmountInCurrency(currencyPair: CurrencyPair, orderType: OrderType, counterAmount: Amount, f: (Amount, ExchangeRate) => Amount): F[Amount] = {
    val ticker = new FallbackBittrexTicker[F](currencyPair)
    val selector = getRateSelector(orderType)
    for {
      rate <- ticker.currentRates
      amount = f(counterAmount, selector(rate))
      _ <- logM(amount, "amount")
    } yield amount
  }

  private def createUndoOrder(order: TradeOrder, midCurrencyAmount: F[Amount]): F[Identifier] = {
    for {
      amount <- midCurrencyAmount
      undoOrder = createFirstSubOrder(order, amount).inverse
      _ <- logM(undoOrder.toString, "UndoOrder")
      undoTxId <- exchange.fulfillOrder(undoOrder)
      _ <- logM(undoTxId, "UndoTransactionId")
    } yield undoTxId
  }

  private def createFirstSubOrder(order: TradeOrder, amount: Amount): TradeOrder = {
    val firstCP = CurrencyPair(order.currencyPair.counter, midCurrency)
    currencyPairAndAmountLens.set(order)(firstCP, amount)
  }

  private def createSecondSubOrder(order: TradeOrder, amount: Amount): TradeOrder = {
    val secondCP = CurrencyPair(midCurrency, order.currencyPair.base)
    currencyPairAndAmountLens.set(order)(secondCP, amount)
  }

  private def getAmountInMidCurrency(order: TradeOrder): F[Amount] = {
    getAmountInCurrency(CurrencyPair(midCurrency, order.currencyPair.base), getOrderType(order), order.amount.amount, _ * _)
  }
}
