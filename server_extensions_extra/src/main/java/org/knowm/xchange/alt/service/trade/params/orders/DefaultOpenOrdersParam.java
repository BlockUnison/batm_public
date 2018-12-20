package org.knowm.xchange.alt.service.trade.params.orders;

import org.knowm.xchange.alt.dto.trade.LimitOrder;

public class DefaultOpenOrdersParam implements OpenOrdersParams {

  public DefaultOpenOrdersParam() {}

  @Override
  public boolean accept(LimitOrder order) {
    return order != null;
  }
}
