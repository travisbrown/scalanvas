package edu.umd.mith.banana.jena.io

import com.hp.hpl.jena.rdf.model.ModelFactory
import edu.umd.mith.banana.io.RDFJson
import java.io.OutputStream
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.Lang
import org.w3.banana.RDFWriter
import org.w3.banana.jena.Jena
import scala.util.Try

trait RDFJsonWriter extends RDFWriter[Jena, RDFJson] {
  val syntax = RDFJson

  def write(graph: Jena#Graph, stream: OutputStream, base: String): Try[Unit] = Try {
    val model = ModelFactory.createModelForGraph(graph)
    RDFDataMgr.write(stream, model, Lang.RDFJSON)
  }
}

