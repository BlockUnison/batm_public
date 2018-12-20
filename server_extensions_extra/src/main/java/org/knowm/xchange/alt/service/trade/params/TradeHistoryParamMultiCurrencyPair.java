package org.knowm.xchange.alt.service.trade.params;

import java.util.Collection;
import org.knowm.xchange.alt.currency.CurrencyPair;

public interface TradeHistoryParamMultiCurrencyPair extends TradeHistoryParams {

  Collection<CurrencyPair> getCurrencyPairs();

  void setCurrencyPairs(Collection<CurrencyPair> pairs);
}
