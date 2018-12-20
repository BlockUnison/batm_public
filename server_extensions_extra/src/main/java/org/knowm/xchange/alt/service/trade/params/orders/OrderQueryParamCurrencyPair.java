package org.knowm.xchange.alt.service.trade.params.orders;

import org.knowm.xchange.alt.currency.CurrencyPair;

public interface OrderQueryParamCurrencyPair extends OrderQueryParams {
  CurrencyPair getCurrencyPair();

  void setCurrencyPair(CurrencyPair currencyPair);
}
