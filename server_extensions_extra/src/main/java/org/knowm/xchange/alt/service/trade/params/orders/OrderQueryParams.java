package org.knowm.xchange.alt.service.trade.params.orders;

import org.knowm.xchange.alt.service.trade.TradeService;

/**
 * Root interface for all interfaces used as a parameter type for {@link
 * TradeService#getOrder(OrderQueryParams...)}.
 * Exchanges should have their own implementation of this interface if querying an order requires
 * information additional to orderId
 *
 * <ul>
 *   <li>{@link OpenOrdersParamCurrencyPair}.
 * </ul>
 */
public interface OrderQueryParams {
  String getOrderId();

  /** Sets the orderId */
  void setOrderId(String orderId);
}
