package com.generalbytes.batm.common.domain

import com.generalbytes.batm.common.factories._

trait Extension
  extends ExchangeFactory
    with RateSourceFactory
    with WalletFactory
    with AddressValidatorFactory {
  val name: String
  val supportedCryptoCurrencies: Set[CryptoCurrency]
}
