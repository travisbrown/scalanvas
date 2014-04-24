package edu.umd.mith.banana.jena.io

import edu.umd.mith.banana.io.JsonLdContext

/** Provides Jena implementations of JSON serializations.
  *
  * @author Travis Brown
  * @todo Should also provide readers. 
  */
package object jena {
  implicit def JsonLdWriter[C: JsonLdContext](implicit ctx: C) = new JsonLdWriter[C] {
    val context = ctx
  }

  implicit def RdfJsonWriter = new RdfJsonWriter {}
}

