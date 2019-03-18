package com.generalbytes.batm.server.extensions.extra.decent.factories

import cats.implicits._
import com.generalbytes.batm.common.adapters.RateSourceAdapter
import com.generalbytes.batm.common.domain.{Attempt, Currency, Task}
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.factories.RateSourceFactory
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced
import com.generalbytes.batm.server.extensions.extra.decent.sources.dct_bittrex.BittrexRateSourceWrapper

trait BittrexRateSourceFactory extends RateSourceFactory with LoggingSupport {
  private val rateSourceLoginData = """dct_bittrex:([A-Z,]+)""".r

  private def parseLoginInfo(loginInfo: String): Option[List[Currency]] = Option(loginInfo).getOrElse("") match {
    case rateSourceLoginData(intermediates) => Currency.parseCSV(intermediates).some
    case _ => none
  }

  def createRateSource(loginInfo: String): Attempt[IRateSourceAdvanced] =
    parseLoginInfo(log(loginInfo)) map { intermediates =>
      new RateSourceAdapter(
        new BittrexRateSourceWrapper[Task](intermediates)
      )
    } toRight err"Could not create exchange from the parameters: $loginInfo"
}
