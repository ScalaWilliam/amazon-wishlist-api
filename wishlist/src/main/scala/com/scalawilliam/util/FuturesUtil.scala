package com.scalawilliam.util

import akka.actor.Scheduler

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.reflectiveCalls
import scala.util._

object FuturesUtil {

  def tryUpTo[T](times: Int)(f: Int => Try[T]): (Try[T], Int) = {
    Iterator.iterate(f(1) -> 1){
      case (trial, no) =>

        trial.transform(a => Try(a), _ => f(no + 1)) -> (no + 1)
    }.take(times).toVector.last
  }

  /** Fulfil the future with value of 'result' after 'delay' **/
  def delayedFuture[T](delay: FiniteDuration)(result: => T)(implicit executor: ExecutionContext, scheduler: Scheduler): Future[T] = {
    val promise = Promise[T]()
    scheduler.scheduleOnce(delay) {
      promise.tryComplete(Try(result))
    }
    promise.future
  }

  /** Fulfil the future with value of 'result' after 'delay' **/
  def delayedFutureF[T](delay: FiniteDuration)(result: => Future[T])(implicit executor: ExecutionContext, scheduler: Scheduler): Future[T] = {
    val promise = Promise[T]()
    scheduler.scheduleOnce(delay) {
      promise.completeWith(result)
    }
    promise.future
  }

  def tryUpToWithDelayNonBlocking[T](times: Int, delay: FiniteDuration)(f: => Future[T])(implicit ec: ExecutionContext, scheduler: Scheduler): Future[(Try[T], Int)] = {

    require(times >= 1, s"Times is $times, should be at least 1")

    def getResult(attemptNo: Int): Future[(Try[T], Int)] = {
      val promise = Promise[(Try[T], Int)]()
      f onComplete {
        case success @ Success(result) =>
          promise.success((success, attemptNo))
        case failure @ Failure(result) if attemptNo == times =>
          promise.success((failure, attemptNo))
        case failure @ Failure(result) =>
          promise.completeWith(delayedFutureF(delay)(getResult(attemptNo + 1)))
      }
      promise.future
    }

    getResult(attemptNo = 1)

  }

  def recursiveFuture[T, V](maxTimes: Int, f: T => Future[V], g: V => Option[T])(startWith: => T)(implicit ec: ExecutionContext): Future[List[V]] = {
    def continue(nextKey: T)(collected: List[V])(left: Int): Future[List[V]] = {
      for {
        v <- f(nextKey)
        newlyCollected = collected :+ v
        nextBit <- g(v) match {
          case Some(key) if left == 0 => Future.successful(newlyCollected)
          case Some(key) => continue(key)(newlyCollected)(left - 1)
          case None => Future.successful(newlyCollected)
        }
      } yield nextBit
    }
    continue(startWith)(Nil)(maxTimes - 1)
  }


  def using[T <: {def close()},V](cbn: => T)(f: T=> V):V = {
    val obj = cbn
    try {
      f(obj)
    } finally {
      obj.close()
    }
  }


}