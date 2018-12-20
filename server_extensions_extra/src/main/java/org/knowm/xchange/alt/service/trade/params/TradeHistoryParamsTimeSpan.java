package org.knowm.xchange.alt.service.trade.params;

import java.util.Date;
import org.knowm.xchange.alt.service.trade.TradeService;

/**
 * Parameters type for {@link TradeService#getTradeHistory(TradeHistoryParams)} with start and end
 * timestamps.
 */
public interface TradeHistoryParamsTimeSpan extends TradeHistoryParams {

  Date getStartTime();

  void setStartTime(Date startTime);

  Date getEndTime();

  void setEndTime(Date endTime);
}
