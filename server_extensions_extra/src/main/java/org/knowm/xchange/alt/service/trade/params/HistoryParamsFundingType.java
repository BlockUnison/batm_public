package org.knowm.xchange.alt.service.trade.params;

import org.knowm.xchange.alt.dto.account.FundingRecord;

public interface HistoryParamsFundingType extends TradeHistoryParams {

  FundingRecord.Type getType();

  void setType(FundingRecord.Type type);
}
