package org.knowm.xchange.alt.service.trade.params;

public class DefaultCancelOrderParamId implements CancelOrderByIdParams {

  private String orderId;

  public DefaultCancelOrderParamId() {}

  public DefaultCancelOrderParamId(String orderId) {

    this.orderId = orderId;
  }

  @Override
  public String getOrderId() {

    return orderId;
  }
}
