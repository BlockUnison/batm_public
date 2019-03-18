package com.generalbytes.batm.server.extensions.extra.decent

import com.generalbytes.batm.common.adapters.ExtensionAdapter
import com.generalbytes.batm.common.domain.{CryptoCurrency, Currency, Extension}
import com.generalbytes.batm.common.factories.DummyAddressValidatorFactory
import com.generalbytes.batm.server.extensions.extra.decent.factories.{BittrexExchangeFactory, BittrexRateSourceFactory, DecentHotWalletFactory}

// WARN: DON'T change the name. Name must end with the word "Extension", because GeneralBytes' API is retarded
// and looks for extensions by suffix "Extension" (yes, really, I'm not kidding).
class DecentExtension extends ExtensionAdapter(new DecentExtension.Impl())

object DecentExtension {
  class Impl extends Extension
    with BittrexRateSourceFactory
    with BittrexExchangeFactory
    with DecentHotWalletFactory
    with DummyAddressValidatorFactory {

    override val name: String = "DCT Extension"
    override val supportedCryptoCurrencies: Set[CryptoCurrency] = Set(Currency.Decent, Currency.Bitcoin)
  }
}