//package com.scalawilliam.wishlist.extract
//
//import java.net.{URI, URL}
//import java.util.Date
//
//import org.hypergraphdb.handle.{WeakManagedHandle, WeakHandle}
//import org.hypergraphdb.query.{And, AtomTypeCondition}
//import org.hypergraphdb.{HGPersistentHandle, HGHandle, HyperGraph}
//import org.hypergraphdb.HGQuery.hg._
//import org.scalatest.{Matchers, WordSpec}
//import scala.beans.BeanProperty
//import collection.JavaConverters._
//class ServiceIT extends WordSpec with Matchers {
//  "stuf" must {
//    "do it" in {
//
//      case class Author(
//      @BeanProperty var firstName: String,
//      @BeanProperty var lastName: String
//                         )
//
//      case class Book
//      (@BeanProperty var title: String,
//       @BeanProperty var author: Author)
//
//      case class JWishlist
//      (
//      @BeanProperty var id: String,
//      @BeanProperty var dateTime: Date,
//      @BeanProperty var uri: URI,
//      @BeanProperty var items: java.util.Map[URI, java.util.List[Book]]
//      ) {
//        def asScala = SWishlist(id, dateTime, uri, items.asScala.mapValues(_.asScala.toList).toMap)
//      }
//
//      case class SWishlist(
//      id: String,
//      dateTime: Date,
//      uri: URI,
//      items: Map[URI, List[Book]]
//      ) {
//        def asJava = JWishlist(id, dateTime, uri, items.mapValues(_.asJava).asJava)
//      }
//
//      val swl = SWishlist("Test", new Date(), new URI("test:uri"), items = Map(new URI("test:uri") -> List(Book("noob", Author("John", "Smith")))))
//
//      val graph = new HyperGraph("hgdb")
//      try {
////        val handle = graph.add(swl)
//        graph.getAll[SWishlist](new AtomTypeCondition(classOf[SWishlist])).asScala foreach println
////        println(graph.get[JWishlist](handle).asScala)
////        val r = graph.getAll[AnyRef](
////          new And(
////            new AtomTypeCondition(classOf[Any]),
////            contains()
////          )
////        )
//
////        val r = graph.getAll[Book](new And(
////          new AtomTypeCondition(classOf[Book])
////        ))
//
////        r.asScala foreach println
//
////        val mybook = Book("Critique of Pure Reason", Author("Emanuel", "Kant"))
////        val handle = graph.add(mybook)
////        println(handle)
////        println(graph.get[Book](handle))
//      }
//      finally {
//        graph.close()
//      }
//////      val hndl = graph.add("Hello World")
////      val hello = graph.get[String](hndl)
////      println(hello.toLowerCase)
//////      graph.close()
//////      val someObject = "Lorem ipsum";
//////      val handle1 = graph.add(someObject)
////      println(graph.get[String](hndl))
//
//    }
//  }
//}
