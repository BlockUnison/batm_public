package com.generalbytes.batm.common.factories

import com.generalbytes.batm.common.domain.Attempt
import com.generalbytes.batm.server.extensions.IExchange

trait ExchangeFactory {
  def createExchange(loginInfo: String): Attempt[IExchange]
}
