package com.generalbytes.batm.server.extensions.extra.decent.utils

import com.generalbytes.batm.common.domain.ExchangeRate
import com.generalbytes.batm.server.extensions.extra.decent.sources.dct_bittrex.BittrexTick
import org.knowm.xchange.dto.Order.OrderType

object BittrexUtils {
  type RateSelector = BittrexTick => ExchangeRate
  def getRateSelector(orderType: OrderType): RateSelector = orderType match {
    case OrderType.ASK => _.ask
    case OrderType.BID => _.bid
  }
}
