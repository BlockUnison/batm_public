package com.generalbytes.batm.server.extensions.extra.decent.sources.dct_bittrex

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.server.extensions.extra.decent.utils.ClientFactory
import io.circe.{Decoder, DecodingFailure}
import org.http4s.Uri
import org.http4s.circe.CirceEntityDecoder._

case class BittrexTick(bid: ExchangeRate, ask: ExchangeRate, last: ExchangeRate)
case class BittrexTickError(message: String) extends Throwable(message)

class BittrexTicker[F[_]: ConcurrentEffect](currencyPair: CurrencyPair) extends LoggingSupport with ClientFactory[F] {
  import BittrexTicker._
  private val uri: Uri = Uri.unsafeFromString("https://bittrex.com/api/v1.1/public/getticker")
      .withQueryParam("market", currencyPair.toString)


  def currentRates: F[BittrexTick] =
    client.use (
      _.expect[Attempt[BittrexTick]](uri)
      .flatTap(x => logM[F](x, currencyPair.toString))
      .unattempt
    )
}

object BittrexTicker {
  import cats.FlatMap
  import cats.syntax.flatMap._

  def failure[A]: Decoder.Result[A] = DecodingFailure("Failure", Nil).asLeft[A]
  val tickDecoder: Decoder[BittrexTick] = Decoder.forProduct3("Bid", "Ask", "Last")(BittrexTick)

  implicit val bittrexTickDecoder: Decoder[BittrexTick] = Decoder.instance { c =>
    c.get[Boolean]("success")
      .ifM(
        c.downField("result").as[BittrexTick](tickDecoder),
        failure)
  }

  implicit val bittrexTickErrorDecoder: Decoder[BittrexTickError] = Decoder.instance { c =>
      FlatMap[Decoder.Result].ifM(c.get[Boolean]("success").map(!_))(           // WTF?
        c.downField("message").as[String].map(BittrexTickError),
        failure)
  }

  implicit val bittrexResponseDecoder: Decoder[Attempt[BittrexTick]] = (bittrexTickDecoder either bittrexTickErrorDecoder).map(_.swap)
}
