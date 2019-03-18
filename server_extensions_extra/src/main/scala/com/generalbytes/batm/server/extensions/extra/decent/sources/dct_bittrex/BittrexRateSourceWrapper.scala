package com.generalbytes.batm.server.extensions.extra.decent.sources.dct_bittrex

import cats.effect.ConcurrentEffect
import cats.instances.list._
import cats.syntax.eq._
import cats.syntax.functor._
import cats.syntax.semigroup._
import cats.syntax.traverse._
import cats.{Applicative, Eval, Later, Now, Semigroup}
import com.generalbytes.batm.common.domain.{ExchangeRate, _}
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.server.extensions.extra.decent.utils.BittrexUtils._
import org.knowm.xchange.dto.Order.OrderType

class BittrexRateSourceWrapper[F[_]: ConcurrentEffect](intermediate: List[Currency])
  extends RateSource[F] with LoggingSupport {
  implicit val bigDecimalMulSemigroup: Semigroup[ExchangeRate] = (x: ExchangeRate, y: ExchangeRate) => x * y

  private def createTicker(orderType: OrderType, currencyPair: CurrencyPair): Eval[F[ExchangeRate]] = {
    if (currencyPair.base === currencyPair.counter)
      Now(Applicative[F].pure(BigDecimal.valueOf(1L)))
    else if (currencyPair.base.isInstanceOf[Fiat] && currencyPair.counter.isInstanceOf[Fiat])
      Later(new FiatCurrencyExchangeTicker[F](currencyPair)).map(_.currentRate)
    else
      Later(new FallbackBittrexTicker[F](currencyPair)).map(_.currentRates.map(getRateSelector(orderType)))
  }

  private def isCurrencyPairSupported(currencyPair: CurrencyPair): Boolean =
    (cryptoCurrencies.toSet[Currency].contains(currencyPair.base) || fiatCurrencies.toSet[Currency].contains(currencyPair.base)) &&
    (cryptoCurrencies.toSet[Currency].contains(currencyPair.counter) || fiatCurrencies.toSet[Currency].contains(currencyPair.counter))

  private def getCurrencySteps(currencyPair: CurrencyPair, between: List[Currency]): List[Currency] =
    currencyPair.counter :: (currencyPair.base :: between.reverse).reverse

  private def getRate(orderType: OrderType, currencyPair: CurrencyPair): F[ExchangeRate] = {
    if (!isCurrencyPairSupported(currencyPair)) {
      raise[F](err"Currency pair $currencyPair is not supported")
    } else {
      val progression = getCurrencySteps(currencyPair, intermediate)

      val currencyPairs = (progression zip progression.tail)
        .map((CurrencyPair.apply _).tupled)
        .filter(cp => cp.base =!= cp.counter)
      logger.debug(currencyPairs.map(_.toString).mkString(" "))

      val rates = for {
        cp <- currencyPairs
      } yield createTicker(orderType, cp).value

      for {
        rs <- rates.sequence
      } yield rs.foldLeft(BigDecimal.valueOf(1L))(_ |+| _)
    }
  }

  override def getExchangeRateForSell(currencyPair: CurrencyPair): F[ExchangeRate] =
    getRate(OrderType.BID, currencyPair)

  override def getExchangeRateForBuy(currencyPair: CurrencyPair): F[ExchangeRate] =
    getRate(OrderType.ASK, currencyPair)

  override val cryptoCurrencies: Set[CryptoCurrency] = Set(Currency.Decent)
  override val fiatCurrencies: Set[FiatCurrency] = Set(Currency.Euro)
  override val preferredFiat: FiatCurrency = Currency.Euro
}
