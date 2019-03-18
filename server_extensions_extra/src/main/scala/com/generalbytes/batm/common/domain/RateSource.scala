package com.generalbytes.batm.common.domain

trait RateSource[F[_]] extends ExchangeBase {
  def getExchangeRateForSell(currencyPair: CurrencyPair): F[ExchangeRate]
  def getExchangeRateForBuy(currencyPair: CurrencyPair): F[ExchangeRate]
}
