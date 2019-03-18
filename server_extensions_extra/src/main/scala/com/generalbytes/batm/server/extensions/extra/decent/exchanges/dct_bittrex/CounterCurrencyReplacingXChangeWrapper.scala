package com.generalbytes.batm.server.extensions.extra.decent.exchanges.dct_bittrex

import cats.effect.ConcurrentEffect
import cats.syntax.apply._
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.adapters.ExchangeAdapterDecorator
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.server.extensions.extra.decent.sources.dct_bittrex.BittrexRateSourceWrapper
import monocle.Lens
import monocle.macros.GenLens
import shapeless.syntax.std.product._

class CounterCurrencyReplacingXChangeWrapper[F[_]: ConcurrentEffect](exchange: Exchange[F],
                                                                     replacements: Seq[CurrencyPair],
                                                                     intermediate: List[Currency])
  extends ExchangeAdapterDecorator[F](exchange) with LoggingSupport {

  private val rateSource = new BittrexRateSourceWrapper[F](intermediate)
  private val replacementMap = replacements.map(_.productElements.tupled).toMap
  private val counterLens: Lens[TradeOrder, Currency] =
    Lens[TradeOrder, CurrencyPair](_.currencyPair)(cp => _.copy(cp)) composeLens
      GenLens[CurrencyPair](_.counter)

  override def fulfillOrder(order: TradeOrder): F[Identifier] = {
    val replacementCurrency = replacementMap.get(counterLens.get(order))
    val newOrder = replacementCurrency.map(c => counterLens.set(c)(order)).getOrElse(order)
    exchange.fulfillOrder(newOrder)
  }

  override def getBalance(currency: Currency): F[Amount] ={
    val replacementCurrency = replacementMap.getOrElse(currency, currency)
    val balance = exchange.getBalance(replacementCurrency)
    val exchangeRate = rateSource.getExchangeRateForSell(CurrencyPair(currency, replacementCurrency))
    balance.map2(exchangeRate)(_ * _)
  }
}
