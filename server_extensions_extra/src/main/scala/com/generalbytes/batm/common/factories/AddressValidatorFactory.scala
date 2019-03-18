package com.generalbytes.batm.common.factories

import com.generalbytes.batm.common.domain.Attempt
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator

trait AddressValidatorFactory {
  def createAddressValidator: Attempt[ICryptoAddressValidator]
}
