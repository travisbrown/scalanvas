package edu.umd.mith.sga.wwa

import edu.umd.mith.banana.jena.DefaultGraphJenaModule
import edu.umd.mith.sga.rdf._
import edu.umd.mith.sga.wwa.util.ShelfmarkMapReader
import org.w3.banana._
import org.w3.banana.syntax._
import java.io.File
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration

// We need to mix in shelfmark-map reading capabilities and Jena support.
object SparqlDemo extends ShelfmarkMapReader with DefaultGraphJenaModule {
  // Make RDF operations available.
  import Ops._

  // Hardcoded file since this is a demo.
  def shelfmarkMapFile = new File("shelfmarks/wwa.txt")

  // First let's bundle up out manifest configuration for convenience.
  trait Dev extends WwaConfiguration
    with DevelopmentConfiguration
    with BodleianImages
    with SgaTei { this: WwaManifest => }

  // We'll read in the list of all pages and group them into "notebooks".
  val notebooks = shelfmarkMap.groupBy(_._1.split("-").head).toList.sortBy(_._1)

  // We'll build a manifest out of each notebook.
  val manifests = notebooks.map {
    case (notebookId, notebookPages) =>
      new PhysicalManifest with Dev {
        val id = notebookId
        val pages = notebookPages
      }
  }

  // Let's grab the first manifest.
  val manifest = manifests.head

  // And create its RDF graph. The graph is "pointed", which just means it comes
  // with a pointer to a specific node (in this case the manifest).
  val pointedGraph: PointedGraph[Rdf] = manifest.toPG

  // Now we create a Sparql engine for this graph (after unpointing it).
  val engine: SparqlEngine[Rdf] = SparqlGraph(pointedGraph.graph)

  // We'll view our output as Turtle strings for simplicity.
  val writer = RDFWriter[Rdf, Turtle]

  // Banana's Sparql query API is asynchronous, so we need an execution context.
  import scala.concurrent.ExecutionContext.Implicits.global

  // Now for a method that will synchronously make a Sparql construct query and
  // give us a graph back.
  def doConstruct(query: String): Rdf#Graph = {
    val result = engine.executeConstruct(SparqlOps.ConstructQuery(query))

    // Keeping it synchronous for simplicity.
    Await.result(result, Duration.Inf)
  }

  // Call `edu.umd.mith.sga.wwaSparqlDemo.demo()` from the console to play.
  def demo(): Unit = {
    // Let's just get all the statements with this canvas as their subject.
    val statementsAboutCanvasQuery = """
      CONSTRUCT WHERE {
        <http://shelleygodwinarchive.org/data/ox/00055/canvas/0001>
        ?predicate
        ?value
      }
    """

    val statementsAboutCanvasResult = doConstruct(statementsAboutCanvasQuery)
    println(writer.asString(statementsAboutCanvasResult, "").get)

    // We can also build up more complex graphs by combining query results.
    val annotationsQuery = """
      PREFIX oa: <http://www.w3.org/ns/openannotation/core/>
      CONSTRUCT {
        ?annotation
        oa:hasTarget
        <http://shelleygodwinarchive.org/data/ox/00055/canvas/0001>.

        ?annotation a oa:Annotation.

        ?annotation oa:hasBody ?body.

      } WHERE {
        ?annotation
        oa:hasTarget
        <http://shelleygodwinarchive.org/data/ox/00055/canvas/0001>.

        ?annotation a oa:Annotation.

        ?annotation oa:hasBody ?body.
      }
    """

    val annotationsResult = doConstruct(annotationsQuery)

    val bothResults = statementsAboutCanvasResult union annotationsResult

    println("\nAnd now for both results:")
    println(writer.asString(bothResults, "").get)
  }
}
