package com.generalbytes.batm.common

import cats._
import cats.arrow.FunctionK
import cats.effect.{ContextShift, Timer}
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.utils.{LoggingSupport, Ops, Util}
import org.slf4j.Logger
import retry.Sleep

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object implicits extends Ops {

  implicit val timerIO: Timer[Task] = Task.timer(global)
  implicit val sleepIO: Sleep[Task] = (delay: FiniteDuration) => Task.sleep(delay)


  implicit val cs: ContextShift[Task] = Task.contextShift(global)
  implicit def currencyEq[T <: Currency]: Eq[T] = Eq.fromUniversalEquals
  implicit val defaultDuration: FiniteDuration = 5 seconds

  private def attempt2Task[A](a: Attempt[A]): Task[A] =
    Task.fromEither(a)

  private def attempt2Id[A](a: Attempt[A]): Id[A] =
    a.getOrThrow

  private def task2Attempt[A](t: Task[A]): Attempt[A] =
    t.attempt
      .unsafeRunSync()

  def attempt2Null[A](a: Attempt[A])(implicit ev: Null <:< A, logger: Logger): Id[A] =
    Util.logObj(a).toOption.orNull

  implicit val g: Attempt ~> Id = FunctionK.lift(attempt2Id _)
  implicit val h: Task ~> Attempt = FunctionK.lift(task2Attempt _)
  implicit val i: Attempt ~> Task = FunctionK.lift(attempt2Task _)
  implicit val f: Task ~> Id = g compose h
}


