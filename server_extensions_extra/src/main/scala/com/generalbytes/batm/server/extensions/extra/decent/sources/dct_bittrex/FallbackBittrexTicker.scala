package com.generalbytes.batm.server.extensions.extra.decent.sources.dct_bittrex

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.utils.LoggingSupport

class FallbackBittrexTicker[F[_]: ConcurrentEffect](currencyPair: CurrencyPair)
  extends LoggingSupport {
  val underlying: BittrexTicker[F] = new BittrexTicker[F](currencyPair)
  lazy val inverse: BittrexTicker[F] = new BittrexTicker[F](currencyPair.flip)

  def currentRates: F[BittrexTick] = underlying.currentRates.handleErrorWith {
    case BittrexTickError("INVALID_MARKET") => inverse.currentRates.map(flipTick).flatTap(t => logM[F](t, currencyPair.flip.toString))
    case e => raise[F](e)
  }

  private def flipTick(tick: BittrexTick): BittrexTick = BittrexTick(1 / tick.ask, 1 / tick.bid, tick.last)
}
