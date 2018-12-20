package org.knowm.xchange.alt.service.marketdata.params;

import java.util.Collection;
import org.knowm.xchange.alt.currency.CurrencyPair;

public interface CurrencyPairsParam extends Params {

  Collection<CurrencyPair> getCurrencyPairs();
}
