package com.generalbytes.batm.common.utils

import cats.implicits._
import cats.effect.Sync
import com.generalbytes.batm.common.domain.{Exchange, Attempt, Error, ErrorMonad}
import com.generalbytes.batm.common.exchanges.RetryingExchangeWrapper
import com.generalbytes.batm.common.utils.Util.logObj
import org.slf4j.Logger
import retry.Sleep
import shapeless.Typeable
import shapeless.syntax.typeable._

import scala.collection.mutable
import scala.collection.JavaConverters._

trait Ops {

  implicit class FuncOps[A, B, C](f: A => B => C) {
    def flip: B => A => C = (b: B) => (a: A) => f(a)(b)
  }

  implicit class Pipe[A](a: => A) {
    def |>[B](f: A => B): B = f(a)
  }

  implicit class AttemptOps[A](a: Attempt[A]) {
    def getOrThrow: A = {
      a.fold(e => throw e, identity)
    }

    def log(implicit logger: Logger): Attempt[A] = {
      logObj(a)
    }

    def getOrNull[A1 >: A](implicit ev: Null <:< A1): A1 = a.toOption.orNull

    def cast[T : Typeable]: Attempt[T] = a.flatMap(_.cast[T].toRight(err"Could not cast"))
  }

  implicit class StringContextOps(sc: StringContext) {
    def err(args: Any*): Throwable = Error(sc.s(args: _*))
  }

  implicit class OptionOps[A](a: Option[A]) {
    def getOrThrow(message: String): A = {
      a.fold(throw new Exception(message))(identity)
    }
  }

  implicit class FlattenAttemptOps[F[_], A](a: F[Attempt[A]])(implicit F: ErrorMonad[F]) {
    def unattempt: F[A] = a.flatMap(_.fold(
      F.raiseError,
      F.pure))
  }

  implicit class ExchangeOps[F[_]: ErrorMonad : Sleep : Sync](ex: Exchange[F]) {
    def withRetries(maxRetries: Int): Exchange[F] = new RetryingExchangeWrapper(ex, maxRetries)
  }

  implicit class EitherOps[A](self: Either[A, A]) {
    def value: A = self.fold(identity, identity)
  }

  implicit class SetExtensions[A](self: Set[A]) {
    def toJavaSet: java.util.Set[A] = mutable.Set.apply(self.toList: _*).asJava
  }
}
