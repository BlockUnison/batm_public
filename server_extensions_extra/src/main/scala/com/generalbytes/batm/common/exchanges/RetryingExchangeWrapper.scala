package com.generalbytes.batm.common.exchanges

import cats.effect.Sync
import com.generalbytes.batm.common.domain.Identifier
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.adapters.ExchangeAdapterDecorator
import com.generalbytes.batm.common.utils.LoggingSupport
import retry._

class RetryingExchangeWrapper[F[_] : Sleep : Sync](exchange: Exchange[F], maxRetries: Int)
  extends ExchangeAdapterDecorator[F](exchange) with LoggingSupport {

  override def fulfillOrder(order: TradeOrder): F[Identifier] =
    retryingOnAllErrors(
      RetryPolicies.limitRetries(maxRetries), logOp[F, Throwable]
    ) {
      exchange.fulfillOrder(order)
    }
}
