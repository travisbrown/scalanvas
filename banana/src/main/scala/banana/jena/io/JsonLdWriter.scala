package edu.umd.mith.banana.jena.io

import com.github.jsonldjava.core.JSONLD //{ JsonLDOptions, JsonLDProcessor }
import com.github.jsonldjava.impl.JenaRDFParser
import com.github.jsonldjava.utils.JSONUtils
import com.hp.hpl.jena.rdf.model.ModelFactory
import edu.umd.mith.banana.io.{ JsonLd, JsonLdContext }
import java.io.{ OutputStream, OutputStreamWriter }
import org.w3.banana.RDFWriter
import org.w3.banana.jena.Jena
import scala.util._

abstract class JsonLdWriter[C: JsonLdContext] extends RDFWriter[Jena, JsonLd] {
  val syntax = JsonLd

  def context: C
  def contextMap = implicitly[JsonLdContext[C]].toMap(context)

  def write(graph: Jena#Graph, stream: OutputStream, base: String): Try[Unit] = Try {
    val model = ModelFactory.createModelForGraph(graph)
    val parser = new JenaRDFParser()
    //val options = new JsonLdOptions()
    val json = JSONLD.fromRDF(model, parser)
    val compacted = JSONLD.compact(json, contextMap)
    val writer = new OutputStreamWriter(stream) 
    JSONUtils.writePrettyPrint(writer, compacted)
  }
}

