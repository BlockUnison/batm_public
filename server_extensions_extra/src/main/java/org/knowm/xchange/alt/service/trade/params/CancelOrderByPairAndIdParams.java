package org.knowm.xchange.alt.service.trade.params;

import org.knowm.xchange.alt.currency.CurrencyPair;

public interface CancelOrderByPairAndIdParams extends CancelOrderByIdParams {
  CurrencyPair getCurrencyPair();
}
