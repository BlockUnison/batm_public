package org.knowm.xchange.alt.coinmate.dto.trade;

import org.knowm.xchange.alt.service.trade.params.TradeHistoryParamCurrencyPair;
import org.knowm.xchange.alt.currency.CurrencyPair;

public class CoinmateTradeHistoryParam implements TradeHistoryParamCurrencyPair {

  CurrencyPair pair;
  int limit = 1000;

  public CoinmateTradeHistoryParam(CurrencyPair pair) {
    this.pair = pair;
  }

  public CoinmateTradeHistoryParam(CurrencyPair pair, int limit) {
    this.pair = pair;
    this.limit = limit;
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    return pair;
  }

  @Override
  public void setCurrencyPair(CurrencyPair pair) {
    this.pair = pair;
  }
}
