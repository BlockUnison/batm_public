package org.knowm.xchange.alt.service.trade.params.orders;

import org.knowm.xchange.alt.currency.CurrencyPair;
import org.knowm.xchange.alt.dto.trade.LimitOrder;

public interface OpenOrdersParamCurrencyPair extends OpenOrdersParams {
  @Override
  default boolean accept(LimitOrder order) {
    return order != null
        && getCurrencyPair() != null
        && getCurrencyPair().equals(order.getCurrencyPair());
  }

  CurrencyPair getCurrencyPair();

  void setCurrencyPair(CurrencyPair pair);
}
