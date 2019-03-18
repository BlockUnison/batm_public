package com.generalbytes.batm.common.ratesources

import cats.Applicative
import com.generalbytes.batm.common.domain.{ErrorApplicative, ExchangeRate}
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.domain.RateSource
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.utils.Util._
import shapeless.syntax.std.product._

class SingleFixedPriceRateSource[F[_] : ErrorApplicative](currencyPair: CurrencyPairF2C, rate: ExchangeRate) extends RateSource[F] {
  override val cryptoCurrencies: Set[CryptoCurrency] = Set(currencyPair.base)
  override val fiatCurrencies: Set[FiatCurrency] = Set(currencyPair.counter)
  override val preferredFiat: FiatCurrency = currencyPair.counter

  override def getExchangeRateForSell(currencyPair: CurrencyPair): F[ExchangeRate] =
    getRate(currencyPair)

  override def getExchangeRateForBuy(currencyPair: CurrencyPair): F[ExchangeRate] =
    getRate(currencyPair)

  private def getRate(currencyPair: CurrencyPair): F[ExchangeRate] = {
    if (currencyPair.toTuple == this.currencyPair.toTuple) {
      Applicative[F].pure(rate)
    } else raise[F](err"Unsupported currency pair $currencyPair")
  }
}
