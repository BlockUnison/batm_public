package org.knowm.xchange.alt.service.trade.params;

import java.util.Collection;
import java.util.Collections;
import org.knowm.xchange.alt.currency.CurrencyPair;

public class DefaultTradeHistoryParamMultiCurrencyPair
    implements TradeHistoryParamMultiCurrencyPair {

  private Collection<CurrencyPair> pairs = Collections.emptySet();

  @Override
  public Collection<CurrencyPair> getCurrencyPairs() {
    return pairs;
  }

  @Override
  public void setCurrencyPairs(Collection<CurrencyPair> value) {
    pairs = value;
  }
}
