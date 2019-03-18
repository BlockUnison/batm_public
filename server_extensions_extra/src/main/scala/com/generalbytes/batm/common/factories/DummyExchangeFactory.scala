package com.generalbytes.batm.common.factories

import cats.implicits._
import com.generalbytes.batm.common.domain.Attempt
import com.generalbytes.batm.common.exchanges.DummyExchange
import com.generalbytes.batm.server.extensions.IExchangeAdvanced

trait DummyExchangeFactory extends ExchangeFactory {
  def createExchange(loginInfo: String): Attempt[IExchangeAdvanced] = new DummyExchange().asRight
}
