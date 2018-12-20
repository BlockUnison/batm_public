package org.knowm.xchange.alt.service.trade.params;

import org.knowm.xchange.alt.dto.Order.OrderType;

public interface CancelOrderByOrderTypeParams extends CancelOrderParams {
  OrderType getOrderType();
}
