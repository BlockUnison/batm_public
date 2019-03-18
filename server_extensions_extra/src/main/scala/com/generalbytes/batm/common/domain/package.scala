package com.generalbytes.batm.common

import cats.effect.IO
import cats.{ApplicativeError, Id, MonadError, ~>}

package object domain {
  case class Error(message: String) extends Throwable(message)

  type Identifier = String
  type Address = String
  type Amount = BigDecimal
  type Task[+A] = IO[A]
  val Task = IO
  type Attempt[+A] = Either[Throwable, A]
  type ExchangeRate = BigDecimal
  type ErrorApplicative[F[_]] = ApplicativeError[F, Throwable]
  def ErrorApplicative[F[_] : ErrorApplicative]: ErrorApplicative[F] = ApplicativeError[F, Throwable]
  type ErrorMonad[F[_]] = MonadError[F, Throwable]
  type Interpreter[F[_]] = F ~> Id
  type Translator[F[_]] = Attempt ~> F

  object Interpreter { def apply[F[_]](implicit ev: F ~> Id): Interpreter[F] = ev }

  object Translator { def apply[F[_]](implicit ev: Attempt ~> F): Translator[F] = ev }
}
