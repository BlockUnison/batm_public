package com.generalbytes.batm.common.adapters

import java.math._
import java.util

import com.generalbytes.batm.common.domain.Interpreter
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced

class RateSourceAdapter[F[_] : Interpreter](rs: RateSource[F]) extends IRateSourceAdvanced {
  private val interpret = implicitly[Interpreter[F]]

  override def getCryptoCurrencies: util.Set[String] = rs.cryptoCurrencies.map(_.name).toJavaSet

  override def getFiatCurrencies: util.Set[String] = rs.fiatCurrencies.map(_.name).toJavaSet

  override def getExchangeRateLast(cryptoCurrency: String, fiatCurrency: String): BigDecimal =
    interpret(rs.getExchangeRateForSell(CurrencyPair.fromNames(cryptoCurrency, fiatCurrency).getOrThrow)).bigDecimal

  override def getPreferredFiatCurrency: String = rs.preferredFiat.name

  override def getExchangeRateForBuy(cryptoCurrency: String, fiatCurrency: String): BigDecimal =
    interpret(rs.getExchangeRateForBuy(CurrencyPair.fromNames(fiatCurrency, cryptoCurrency).getOrThrow)).bigDecimal

  override def getExchangeRateForSell(cryptoCurrency: String, fiatCurrency: String): BigDecimal =
    interpret(rs.getExchangeRateForBuy(CurrencyPair.fromNames(fiatCurrency, cryptoCurrency).getOrThrow)).bigDecimal

  override def calculateBuyPrice(cryptoCurrency: String, fiatCurrency: String, amount: BigDecimal): BigDecimal =
    getExchangeRateForBuy(fiatCurrency, cryptoCurrency).multiply(amount)

  override def calculateSellPrice(cryptoCurrency: String, fiatCurrency: String, amount: BigDecimal): BigDecimal =
    getExchangeRateForSell(fiatCurrency, cryptoCurrency).multiply(amount)
}
