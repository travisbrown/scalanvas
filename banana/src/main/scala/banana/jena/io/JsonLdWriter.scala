package edu.umd.mith.banana.jena.io

import com.github.jsonldjava.core.{ JsonLdOptions, JsonLdProcessor }
import com.github.jsonldjava.jena.JenaRDFParser
import com.github.jsonldjava.utils.JsonUtils
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
    val options = new JsonLdOptions()
    val json = JsonLdProcessor.fromRDF(model, parser)
    val compacted = JsonLdProcessor.compact(json, contextMap, options)
    val writer = new OutputStreamWriter(stream) 
    JsonUtils.writePrettyPrint(writer, compacted)
  }
}

