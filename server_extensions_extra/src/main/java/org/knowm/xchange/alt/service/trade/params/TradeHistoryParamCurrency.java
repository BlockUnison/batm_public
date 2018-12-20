package org.knowm.xchange.alt.service.trade.params;

import org.knowm.xchange.alt.currency.Currency;

public interface TradeHistoryParamCurrency extends TradeHistoryParams {

  Currency getCurrency();

  void setCurrency(Currency currency);
}
