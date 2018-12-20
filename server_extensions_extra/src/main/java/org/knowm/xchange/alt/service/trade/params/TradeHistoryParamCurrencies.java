package org.knowm.xchange.alt.service.trade.params;

import org.knowm.xchange.alt.currency.Currency;

public interface TradeHistoryParamCurrencies extends TradeHistoryParams {

  Currency[] getCurrencies();

  void setCurrencies(Currency[] currencies);
}
