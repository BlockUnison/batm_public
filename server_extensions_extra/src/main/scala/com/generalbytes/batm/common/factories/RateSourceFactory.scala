package com.generalbytes.batm.common.factories

import com.generalbytes.batm.common.domain.Attempt
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced

trait RateSourceFactory {
  def createRateSource(loginInfo: String): Attempt[IRateSourceAdvanced]
}
