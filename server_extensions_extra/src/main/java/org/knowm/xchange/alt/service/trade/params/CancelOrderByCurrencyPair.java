package org.knowm.xchange.alt.service.trade.params;

import org.knowm.xchange.alt.currency.CurrencyPair;

public interface CancelOrderByCurrencyPair extends CancelOrderParams {

  public CurrencyPair getCurrencyPair();
}
