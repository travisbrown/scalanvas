package edu.umd.mith.banana

import org.w3.banana.{ MimeType, Syntax }
import scalaz.NonEmptyList

/** Provides syntaxes for JSON serialization.
  *
  * @author Travis Brown
  */
package object io {
  implicit val JsonLd = new Syntax[JsonLd] {
    val mimeTypes = NonEmptyList(MimeType("application/ld+json"))
  }

  implicit val RdfJson = new Syntax[RdfJson] {
    val mimeTypes = NonEmptyList(MimeType("application/rdf+json"))
  }

  implicit object NamespaceMapJsonLdContext extends JsonLdContext[Map[String, String]] {
    def toMap(context: Map[String, String]): java.util.Map[String, Object] = {
      val javaContext = new java.util.HashMap[String, Object]()
      context.foreach { case (k, v) => javaContext.put(k, v) }
      javaContext
    }
  }

  implicit object NamespaceJavaMapJsonLdContext
    extends JsonLdContext[java.util.Map[String, Object]] {
    def toMap(context: java.util.Map[String, Object]) = context
  }
}

