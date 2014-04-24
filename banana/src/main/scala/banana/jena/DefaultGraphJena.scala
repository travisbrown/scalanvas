package edu.umd.mith.banana.jena

import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.graph.Factory
import org.w3.banana.RDFOps
import org.w3.banana.jena.{ Jena, JenaModule, JenaOperations }

object DefaultGraphJena extends DefaultGraphJenaModule

/** Replaces Banana's custom implementation of [[com.hp.hpl.jena.graph.Graph]]
  * with the default graph implementation provided by Jena. This may be
  * necessary for performance in some situations.
  */
trait DefaultGraphJenaModule extends JenaModule {
  implicit override val Ops: JenaOperations = DefaultGraphJenaOperations
}

object DefaultGraphJenaOperations extends DefaultGraphJenaOperations

/** Overrides methods that introduce [[org.w3.banana.jena.ImmutableJenaGraph]].
  *
  * We don't provide an implementation of graph difference here, but it would
  * be a reasonable thing to do.
  *
  * These implementations are copied with minor modifications from an earlier
  * version of [[org.w3.banana.jena.JenaOperations]].
  */
trait DefaultGraphJenaOperations extends JenaOperations {
  override val emptyGraph = Factory.createDefaultGraph()

  override def makeGraph(triples: Iterable[Jena#Triple]) = {
    val graph = Factory.createDefaultGraph()
    triples.foreach(graph.add)
    graph
  }

  override def union(graphs: Seq[Jena#Graph]) = {
    val unionGraph = Factory.createDefaultGraph()
    graphs.foreach { graph =>
      val triples = graph.find(Node.ANY, Node.ANY, Node.ANY)
      while (triples.hasNext()) {
        unionGraph.add(triples.next())
      }
    }
    unionGraph
  }
}

