package com.generalbytes.batm.server.extensions.extra.decent.exchanges.dct_bittrex

import com.generalbytes.batm.common.adapters.ExchangeAdapter
import com.generalbytes.batm.common.domain.Task
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.server.extensions.extra.decent.factories.Credentials

class AdaptedBittrexExchange(credentials: Credentials) extends ExchangeAdapter(new BittrexXChangeWrapper[Task](credentials))
