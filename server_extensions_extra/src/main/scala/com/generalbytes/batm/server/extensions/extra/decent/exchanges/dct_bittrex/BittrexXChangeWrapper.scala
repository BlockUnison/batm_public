package com.generalbytes.batm.server.extensions.extra.decent.exchanges.dct_bittrex

import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.common.utils.XChangeUtils._
import com.generalbytes.batm.server.extensions.extra.decent.factories.Credentials
import com.generalbytes.batm.server.extensions.extra.decent.sources.dct_bittrex.{BittrexTick, FallbackBittrexTicker}
import org.knowm.xchange.{Exchange => XChange, ExchangeFactory}
import org.knowm.xchange.bittrex.BittrexExchange
import org.knowm.xchange.bittrex.dto.trade.BittrexOrder
import org.knowm.xchange.bittrex.service.BittrexAccountServiceRaw
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.trade.LimitOrder
import retry._

class BittrexXChangeWrapper[F[_]: Sleep : ConcurrentEffect](credentials: Credentials)
  extends Exchange[F] with LoggingSupport {

  import com.generalbytes.batm.server.extensions.extra.decent.utils.BittrexUtils._

  protected val exchange: XChange = createExchange

  protected def createExchange: XChange = {
    val spec = new BittrexExchange().getDefaultExchangeSpecification
    spec.setApiKey(credentials.apiKey)
    spec.setSecretKey(credentials.secretKey)
    val result = ExchangeFactory.INSTANCE.createExchange(spec)
    assert(result != null)
    result
  }

  private def multiplicativeFactor(orderType: OrderType): ExchangeRate = {
    val additive = 0.2
    orderType match {
      case OrderType.BID => 1d + additive |> BigDecimal.valueOf
      case OrderType.ASK => 1d - additive |> BigDecimal.valueOf
    }
  }

  private def getOrder(id: Identifier): F[BittrexOrder] = delay {
    exchange.getAccountService.asInstanceOf[BittrexAccountServiceRaw].getBittrexOrder(id)
  }

  override def getBalance(currency: Currency): F[Amount] = delay {
    val balance = exchange.getAccountService.getAccountInfo.getWallet.getBalance(currency.convert)
    BigDecimal(balance.getTotal)
  }

  override def getAddress(currency: Currency): F[Address] = raise[F](err"Not implemented")

  // TODO: Make cancellable
  // TODO: IO.shift to another thread to avoid blocking
  override def fulfillOrder(order: TradeOrder): F[Identifier] = {
    val orderId: F[Identifier] = Async.memoize {
      createLimitOrder(order).map(exchange.getTradeService.placeLimitOrder)
    }.toIO.unsafeRunSync() // only to initialize memoization, doesn't make the actual call

    val maxAttempts = 10
    val polling = retryingM[BittrexOrder](
      RetryPolicies.limitRetries[F](maxAttempts),
      r => !r.getOpen,
      logOp[F, BittrexOrder]) {
      for {
        ordId <- orderId
        _ <- logM(ordId, s"Order ID for $order ")
        order <- getOrder(ordId)
      } yield order
    } map (_.getOrderUuid)

    polling.handleErrorWith(e => raise[F](BittrexXChangeWrapper.ErrorDecorator(e, order.currencyPair)))
  }

  override def withdrawFunds(currency: Currency, amount: Amount, destination: Address): F[Identifier] = delay {
    exchange.getAccountService.withdrawFunds(currency.convert, amount.bigDecimal, destination)
  }

  protected def createLimitOrder(order: TradeOrder): F[LimitOrder] = {
    val ticker = new FallbackBittrexTicker[F](order.currencyPair)
    for {
      rate <- ticker.currentRates
      orderType = getOrderType(order)
      amount = order.amount.amount
      price = calculateLimitPrice(rate, orderType)
      _ <- logM(price, "limit price")
    } yield new LimitOrder.Builder(orderType, order.currencyPair.convert)
      .originalAmount(amount.bigDecimal)
      .limitPrice(price.bigDecimal)
      .build()
  }

  // NOTE: We specify the limit price to the OPPOSITE ticker ask->bid bid->ask, because there's no market order
  protected def calculateLimitPrice(tick: BittrexTick, orderType: OrderType): Amount = {
    val rateSelector = getRateSelector(orderType.inverse)
    val mulFactor = multiplicativeFactor(orderType)
    val rate = rateSelector(tick) * mulFactor
    val significantDigits = 4
    val newScale = significantDigits - rate.precision + rate.scale
    rate.setScale(newScale, scala.BigDecimal.RoundingMode.HALF_UP)
  }

  override val cryptoCurrencies: Set[CryptoCurrency] = Set.empty
  override val fiatCurrencies: Set[FiatCurrency] = Set.empty
  override val preferredFiat: FiatCurrency = Currency.USDollar
}

object BittrexXChangeWrapper {
  case class ErrorDecorator(error: Throwable, currencyPair: CurrencyPair) extends Throwable(error)
}

