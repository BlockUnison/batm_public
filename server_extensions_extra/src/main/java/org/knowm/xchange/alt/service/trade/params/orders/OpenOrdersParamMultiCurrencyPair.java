package org.knowm.xchange.alt.service.trade.params.orders;

import java.util.Collection;
import org.knowm.xchange.alt.currency.CurrencyPair;
import org.knowm.xchange.alt.dto.trade.LimitOrder;

public interface OpenOrdersParamMultiCurrencyPair extends OpenOrdersParams {
  @Override
  default boolean accept(LimitOrder order) {
    return order != null
        && getCurrencyPairs() != null
        && getCurrencyPairs().contains(order.getCurrencyPair());
  }

  Collection<CurrencyPair> getCurrencyPairs();

  void setCurrencyPairs(Collection<CurrencyPair> pairs);
}
