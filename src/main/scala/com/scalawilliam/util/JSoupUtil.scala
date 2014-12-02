package com.scalawilliam.util

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

object JSoupUtil {
  implicit class simplifyElements(a: Elements) {
    def optionalText =
      a.asScala.map(_.text()).map(_.trim).filterNot(_.isEmpty).headOption
    def optionalAttr(name: String) =
      a.asScala.map(_.attr(name)).map(_.trim).filterNot(_.isEmpty).headOption
  }
  implicit class simplifyElement(a: Element) {
    def optionalText =
      Option(a.text()).map(_.trim).filterNot(_.isEmpty).headOption
    def optionalAttr(name: String) =
      Option(a.attr(name)).map(_.trim).filterNot(_.isEmpty).headOption
  }

}
