package edu.umd.mith.scalanvas.rdf

//import scalaz.{ Source => _, _ }, Scalaz._

import edu.umd.mith.scalanvas.model._

import org.w3.banana._
import org.w3.banana.binder._
import org.w3.banana.diesel._
import org.w3.banana.syntax._

trait PropertyBinders { this: ObjectBinders =>
  trait ResourceToPG[Rdf <: RDF, A <: Resource] extends ToPG[Rdf, A] {
    this: ScalanvasPrefixes[Rdf] =>

    def toPG(a: A) = PointedGraph(
      a.muri.fold[Rdf#Node](ops.bnode())(uri => ops.URI(uri.toString))
    )
  }

  trait RectToPG[Rdf <: RDF, A <: Rect] extends ToPG[Rdf, A] {
    this: ScalanvasPrefixes[Rdf] =>

    abstract override def toPG(a: A) = (
      super.toPG(a)
        -- exif.width ->- a.width
        -- exif.height ->- a.height
    )
  }

  trait LabeledToPG[Rdf <: RDF, A <: Labeled] extends ToPG[Rdf, A] {
    this: ScalanvasPrefixes[Rdf] =>

    abstract override def toPG(a: A) = (
      super.toPG(a) -- rdfs.label ->- a.label
    )
  }

  trait FormattedToPG[Rdf <: RDF, A <: Formatted] extends ToPG[Rdf, A] {
    this: ScalanvasPrefixes[Rdf] =>

    abstract override def toPG(a: A) = (
      super.toPG(a) -- dc.format ->- a.format
    )
  }

  trait HasRelatedServiceToPG[Rdf <: RDF, A <: HasRelatedService] extends ToPG[Rdf, A] {
    this: ScalanvasPrefixes[Rdf] =>

    abstract override def toPG(a: A) = (
      super.toPG(a) -- sc.hasRelatedService ->- a.service
    )
  }

  trait MetadataLabeledToPG[Rdf <: RDF, A <: MetadataLabeled] extends ToPG[Rdf, A] {
    this: ScalanvasPrefixes[Rdf] =>

    abstract override def toPG(a: A) = (
      super.toPG(a)
        -- sc.agentLabel ->- a.agent
        -- sc.attributionLabel ->- a.attribution
      -- sc.dateLabel ->- a.date
    )
  }
}

