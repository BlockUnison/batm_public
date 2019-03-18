package com.generalbytes.batm.server.extensions.extra.decent.factories

import cats.implicits._
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.adapters.ExchangeAdapter
import com.generalbytes.batm.common.factories.ExchangeFactory
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.server.extensions.IExchange
import com.generalbytes.batm.server.extensions.extra.decent.exchanges.dct_bittrex._

case class Credentials(apiKey: String, secretKey: String)
case class ExchangeParams(replacements: List[CurrencyPair], intermediates: List[Currency], credentials: Credentials)

trait BittrexExchangeFactory extends ExchangeFactory with LoggingSupport {

  private val exchangeLoginData = """dct_bittrex:([A-Za-z0-9]+):([A-Za-z0-9]+):([A-Z,->]+):([A-Z,]+)""".r

  private def getCurrencyPair(pair: String): Option[CurrencyPair] = {
    val list = Currency.parseCSV(pair.replace("->", ","))
    if (list.length != 2) none
    else CurrencyPair(list.head, list.tail.head).some
  }

  protected def parseExchangeLoginInfo(loginInfo: String): Option[ExchangeParams] = Option(loginInfo).getOrElse("") match {
    case exchangeLoginData(apiKey, secretKey, replacements, intermediates) => ExchangeParams(
        replacements.split(',').toList.flatMap(s => getCurrencyPair(s).toList),
        Currency.parseCSV(intermediates),
        Credentials(apiKey, secretKey)
      ).some
    case _ => none
  }

  def createExchange(loginInfo: String): Attempt[IExchange] = {
    parseExchangeLoginInfo(log(loginInfo))
      .map(params =>
        new ExchangeAdapter(
          new CounterCurrencyReplacingXChangeWrapper(
            new BittrexXChangeWrapper[Task](params.credentials),
              params.replacements,
              params.intermediates
//              List(CurrencyPair(Euro, Bitcoin), CurrencyPair(USDollar, Bitcoin)),
//              List(USDollar)
          )
        )
      )
      .toRight(err"Could not create exchange from params: $loginInfo")
  }
}
