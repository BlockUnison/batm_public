package org.knowm.xchange.alt.service.trade.params;

public interface TradeHistoryParamNextPageCursor extends TradeHistoryParams {
  String getNextPageCursor();

  void setNextPageCursor(String cursor);
}
