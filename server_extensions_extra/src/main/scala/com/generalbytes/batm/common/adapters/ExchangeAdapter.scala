package com.generalbytes.batm.common.adapters

import java.math._
import java.util

import cats._
import cats.implicits._
import com.generalbytes.batm.common.domain.{Interpreter, _}
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.server.extensions.IExchange

class ExchangeAdapter[F[_] : Monad : Interpreter : Translator](xch: Exchange[F]) extends IExchange with LoggingSupport {
  private val interpret = implicitly[Interpreter[F]]

  override def getCryptoCurrencies: util.Set[String] = xch.cryptoCurrencies.map(_.name).toJavaSet

  override def getFiatCurrencies: util.Set[String] = xch.fiatCurrencies.map(_.name).toJavaSet

  override def getPreferredFiatCurrency: String = xch.preferredFiat.name

  override def getCryptoBalance(cryptoCurrency: String): BigDecimal =
    interpret(xch.getBalance(Currency.withName(cryptoCurrency).getOrThrow).map(_.bigDecimal))

  override def getFiatBalance(fiatCurrency: String): BigDecimal =
    interpret(xch.getBalance(Currency.withName(fiatCurrency).getOrThrow).map(_.bigDecimal))

  override def purchaseCoins(amount: BigDecimal, cryptoCurrency: String, fiatCurrency: String, description: String): String = {
    val order: Attempt[TradeOrder] = createOrder(amount, cryptoCurrency, fiatCurrency, TradeOrder.buy)

    processOrder(order)
  }

  override def sellCoins(amount: BigDecimal, cryptoCurrency: String, fiatCurrency: String, description: String): String = {
    val order: Attempt[TradeOrder] = createOrder(amount, cryptoCurrency, fiatCurrency, TradeOrder.sell)

    processOrder(order)
  }

  private def createOrder[T <: Currency](amount: BigDecimal, cryptoCurrency: String, fiatCurrency: String,
                                         constr: (CryptoCurrency, Currency, Amount) => TradeOrder): Attempt[TradeOrder] = {
    for {
      crypto <- Currency.withName(cryptoCurrency).cast[CryptoCurrency]
      fiat <- Currency.withName(fiatCurrency).cast[FiatCurrency]
    } yield constr(crypto, fiat, amount)
  }

  private def processOrder[T <: Currency](order: Attempt[TradeOrder]): Identifier = {
    val txId = for {
      ord <- Translator[F].apply(order)
      res <- xch.fulfillOrder(ord)
    } yield res

    interpret(txId)
  }

  override def sendCoins(destinationAddress: String, amount: BigDecimal, cryptoCurrency: String, desc: String): String = {
    logger.debug(s"Description: $desc, Destination: $destinationAddress")
    val withdrawal: F[Identifier] = for {
      crypto <- Translator[F].apply(Currency.withName(cryptoCurrency))
      res <- xch.withdrawFunds(crypto, scala.BigDecimal(amount), destinationAddress)
    } yield res

    interpret(withdrawal)
  }

  override def getDepositAddress(s: String): String = ???
}
