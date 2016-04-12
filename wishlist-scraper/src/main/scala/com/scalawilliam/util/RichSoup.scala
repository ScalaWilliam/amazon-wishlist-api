package com.scalawilliam.util

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

trait RichSoup {

  implicit class simplifyElements(a: Elements) {
    def optionalText: Option[String] =
      a.asScala.flatMap(_.optionalText).headOption

    def optionalAttr(name: String): Option[String] =
      a.asScala.flatMap(_.optionalAttr(name)).headOption
  }

  implicit class simplifyElement(a: Element) {
    def optionalText: Option[String] =
      Option(a.text()).map(_.trim).filter(_.nonEmpty)

    def optionalAttr(name: String): Option[String] =
      Option(a.attr(name)).map(_.trim).filter(_.nonEmpty)
  }

}

object RichSoup extends RichSoup