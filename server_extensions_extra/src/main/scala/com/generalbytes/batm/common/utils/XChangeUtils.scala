package com.generalbytes.batm.common.utils

import com.generalbytes.batm.common.domain._
import org.knowm.xchange._
import org.knowm.xchange.currency
import org.knowm.xchange.dto.Order.OrderType

object XChangeUtils {
  implicit class CurrencyConv(c: Currency) {
    def convert: currency.Currency = currency.Currency.getInstance(c.name)
  }

  implicit class CurrencyPairConv(cp: CurrencyPair) {
    def convert: currency.CurrencyPair = new currency.CurrencyPair(cp.base.name, cp.counter.name)
  }

  implicit class OrderTypeOps(orderType: OrderType) {
    def inverse: OrderType = orderType match {
      case OrderType.ASK => OrderType.BID
      case OrderType.BID => OrderType.ASK
    }
  }

  def getOrderType(order: TradeOrder): OrderType = order match {
    case _:PurchaseOrder => OrderType.BID
    case _:SaleOrder => OrderType.ASK
  }
}
