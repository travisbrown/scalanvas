package edu.umd.mith.sga.rdf

import edu.umd.mith.sga.model.SgaCanvas
import org.w3.banana._
import org.w3.banana.binder._
import org.w3.banana.diesel._
import org.w3.banana.syntax._
import scalaz._, Scalaz._

class ZoneReader[Rdf <: RDF](canvas: SgaCanvas)(implicit ops: RDFOps[Rdf])
  extends SgaPrefixes[Rdf]
  with SpecificResourceHelper[Rdf]
  with AnnotationHelper[Rdf] {

  val zones = canvas.transcription.map(_ \\ "zone").getOrElse(Nil)

  val typeCounts = zones.map(
    zone => (zone \ "@type").text
  ).groupBy(identity).map {
    case (key, values) => key -> values.size  
  }.withDefaultValue(0)

  val needExtraTop = if (typeCounts("top_marginalia_left") > 0 || typeCounts("top_marginalia_right") > 0) 
    true
  else
    false

  val extraRight = if (typeCounts("marginalia_right") > 0) 0.1 else 0.0

  val extraLeft = if (typeCounts("marginalia_left") > 0) 0.1 else 0.0

  val topHeight = if (needExtraTop) 0.10 else 0.05

  private def coords(current: String, past: List[String]) = (current, past) match {
    case ("running_head", past) =>
      val runningHeadCount = typeCounts("running_head")
      val runningHeadIdx = past.count(_ == "running_head") + 1
      Some(
        ((runningHeadIdx.toDouble / runningHeadCount) - (1 / runningHeadCount.toDouble),  if (needExtraTop) 0.05 else 0.0),
        (1 / runningHeadCount.toDouble, topHeight)
      ).success
    case ("pagination", _) =>
      Some((0.8, 0.0) -> (0.1, topHeight)).success
    case ("library", _) => Some((0.9, 0.0) -> (0.1, topHeight)).success
    case ("left_margin", past) =>
      val leftMarginCount = typeCounts("left_margin")
      val leftMarginIdx = past.count(_ == "left_margin")
      Some(
        (0.0,  topHeight + (1 - topHeight) * (leftMarginIdx.toDouble / leftMarginCount)),
        (0.25, (1 - topHeight) / leftMarginCount)
      ).success
    case ("main", _) if typeCounts("left_margin") == 0 =>
      Some((0.125, topHeight) -> (0.875, 1 - topHeight)).success
    case ("main", _) => Some((0.25, topHeight) -> (0.75, 1 - topHeight)).success
    case ("logical", _) => None.success
    case ("marginalia_left", _) => None.success
    case ("marginalia_right", _) => None.success
    case ("top_marginalia_left", _) => Some((0.0, 0.0) -> (1.0, topHeight)).success
    case ("top_marginalia_right", _) => Some((0.5, 0.0) -> (1.0, topHeight)).success
    case ("column", past) => 
      val columnCount = typeCounts("column")
      val columnIdx = past.count(_ == "column") + 1
      val start = 0 + extraLeft
      val end = 1 - extraRight
      val area = (end - start) / columnCount.toDouble
      Some(
        ((area * (columnIdx.toDouble - 1) + extraLeft,  topHeight),
        (area, 1 - topHeight))
      ).success
    case ("", _) => None.success
    case other => 
       "Unknown zone in %s: %s!".format(canvas.shelfmark, other).fail
  }

  def readZones: List[PointedGraph[Rdf]] =
    zones.foldLeft(
      List.empty[PointedGraph[Rdf]],
      List.empty[String]
    ) {
      case ((annotations, past), zone) =>
        val zoneType = (zone \ "@type").text

        coords(zoneType, past).fold(
          message => throw new RuntimeException(message),
          {
            case Some(((x, y), (zw, zh))) =>
              val xywh = "xywh=%d,%d,%d,%d".format(
                (x * canvas.width).toInt,
                (y * canvas.height).toInt,
                (zw * canvas.width).toInt,
                (zh * canvas.height).toInt
              )

              val attrs = zone.attributes.asAttrMap

              val annotation = contentAnnotation(
                textOffsetSelection(
                  canvas.source,
                  attrs("mu:b").toInt,
                  attrs("mu:e").toInt
                ),
                fragmentSelection(canvas, xywh)
              )

              (annotations :+ annotation, zoneType :: past)
            case _ => (annotations, zoneType :: past)
          }
        )
    }._1
}

