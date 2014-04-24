package edu.umd.mith.banana.io

/** Represents JSON-LD serialization. */
trait JsonLd

/** Represents Talis's RDF JSON serialization. */
trait RdfJson

/** Represents evidence that a type can be used as a JSON-LD context. */
trait JsonLdContext[C] {
  def toMap(context: C): java.util.Map[String, Object]
}

