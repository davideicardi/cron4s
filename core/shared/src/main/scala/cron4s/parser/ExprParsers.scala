package cron4s.parser

import cron4s.{CronField, CronUnit}
import cron4s.expr._

import scala.util.parsing.combinator.RegexParsers
import scala.language.higherKinds
import scalaz.{NonEmptyList, Failure => Failurez, Success => Successz}

/**
  * Created by alonsodomin on 01/01/2016.
  */
trait ExprParsers extends RegexParsers {
  import CronField._
  import CronUnit._

  // Unit parsers

  def second: Parser[ConstExpr[Second.type]] =
    positioned("""[0-5]?\d""".r ^^ { value => ConstExpr(Second, value.toInt) })

  def minute: Parser[ConstExpr[Minute.type]] =
    positioned("""[0-5]?\d""".r ^^ { value => ConstExpr(Minute, value.toInt) })

  def hour: Parser[ConstExpr[Hour.type]] =
    positioned("""2[0-3]|[01]?[0-9]""".r ^^ { value => ConstExpr(Hour, value.toInt) })

  def dayOfMonth: Parser[ConstExpr[DayOfMonth.type]] =
    positioned("""3[01]|[012]?\d""".r ^^ { value => ConstExpr(DayOfMonth, value.toInt) })

  private[this] def numericMonth: Parser[ConstExpr[Month.type]] =
    positioned("""1[0-2]|[1-9]""".r ^^ { value => ConstExpr(Month, value.toInt) })

  private[this] def textMonth: Parser[ConstExpr[Month.type]] =
    positioned(Months.textValues.mkString("|").r ^^ { v =>
      val value = Months.textValues.indexOf(v) + 1
      ConstExpr(Month, value, Some(v))
    })

  def month: Parser[ConstExpr[Month.type]] = numericMonth | textMonth

  private[this] def numberDayOfWeek: Parser[ConstExpr[DayOfWeek.type]] =
    positioned("""[0-6]""".r ^^ { value => ConstExpr(DayOfWeek, value.toInt) })

  private[this] def textDayOfWeek: Parser[ConstExpr[DayOfWeek.type]] =
    positioned(DaysOfWeek.textValues.mkString("|").r ^^ { v =>
      val value = DaysOfWeek.textValues.indexOf(v)
      ConstExpr(DayOfWeek, value, Some(v))
    })

  def dayOfWeek: Parser[ConstExpr[DayOfWeek.type]] = numberDayOfWeek | textDayOfWeek

  // Part parsers

  def any[F <: CronField](implicit unit: CronUnit[F]): Parser[AnyExpr[F]] =
    positioned("""\*""".r ^^ { _ => AnyExpr[F]() })

  def between[F <: CronField](p: Parser[ConstExpr[F]])
      (implicit unit: CronUnit[F]): Parser[BetweenExpr[F]] =
    positioned(p ~ ("-" ~> p) ^^ { case min ~ max => BetweenExpr(min, max) })

  def several[F <: CronField](p: Parser[EnumerableExpr[F]])
      (implicit unit: CronUnit[F]): Parser[SeveralExpr[F]] = {
    positioned(p ~ (("," ~> p)+) ^^ { case head ~ tail =>
      SeveralExpr[F](NonEmptyList(head, tail: _*))
    } ^? ({
      case Successz(expr) => expr
    }, {
      case Failurez(errors) => errors.list.toList.map(_.msg).mkString("\n")
      case Successz(_) => sys.error("received a scalaz.Success when handling an error") // should not happen
    }))
  }

  def every[F <: CronField](p: Parser[DivisibleExpr[F]])
      (implicit unit: CronUnit[F]): Parser[EveryExpr[F]] =
    positioned(p ~ """\/\d+""".r ^^ { case base ~ freq => EveryExpr(base, freq.substring(1).toInt) })

}