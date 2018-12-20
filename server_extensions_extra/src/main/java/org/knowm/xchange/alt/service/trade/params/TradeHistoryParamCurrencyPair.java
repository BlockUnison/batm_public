package org.knowm.xchange.alt.service.trade.params;

import org.knowm.xchange.alt.currency.CurrencyPair;

public interface TradeHistoryParamCurrencyPair extends TradeHistoryParams {

  CurrencyPair getCurrencyPair();

  void setCurrencyPair(CurrencyPair pair);
}
