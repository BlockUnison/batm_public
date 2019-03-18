package com.generalbytes.batm.common.domain

trait ExchangeBase {
  val cryptoCurrencies: Set[CryptoCurrency]
  val fiatCurrencies: Set[FiatCurrency]
  val preferredFiat: FiatCurrency
}
