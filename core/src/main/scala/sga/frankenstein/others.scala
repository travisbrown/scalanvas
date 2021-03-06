package edu.umd.mith.sga.frankenstein

import com.github.jsonldjava.utils.JSONUtils
import org.w3.banana._
import org.w3.banana.syntax._
import edu.umd.mith.scalanvas.model.{ ImageForPainting, Link, Sequence }
import edu.umd.mith.sga.model.{ SgaCanvas, SgaManifest }
import edu.umd.mith.sga.rdf._
import java.io.{ File, FileReader, PrintWriter }
import au.com.bytecode.opencsv.CSVReader

object SpreadsheetReader extends App {
  import scala.collection.JavaConverters._

  val input = new CSVReader(new FileReader("image-metadata.csv"), ';') 
  val data: List[Array[String]] = input.readAll.asScala.toList

  val manifest = new SgaManifest {
    val base = new java.net.URI("http://shelleygodwinarchive.org/example")
    val id = "example"
    val title = "Example manifest"
    val label = "Example manifest"
    val ranges = Nil
    override val hasTranscriptions = false
    val service = None
    val sequence = Sequence[SgaCanvas](
      None,
      "Primary sequence",
      data.tail.zipWithIndex.map {
        case (fields, index) => new SgaCanvas {
          val service = None
          val uri = new java.net.URI(base + "/canvas/%04d".format(index + 1))
          val seq = "%04d".format(index + 1)
          val label = fields(3)
          override val shelfmark = Some(fields(9)) 
          override val folio = Some(fields(3))
          override val hand = Some(fields(7))
          override val agent = Some(fields(6))
          override val attribution = Some(fields(10))
          override val date = Some(fields(8))
          val reading = None //Link(new java.net.URI(base + "/canvas/%04d.html".format(index + 1)), "text/html")
          val source = None //Link(new java.net.URI(base + "/canvas/%04d.xml".format(index + 1)), "application/tei+xml")
          val transcription = None
          val width = fields(1).toInt
          val height = fields(2).toInt
          val images = List(ImageForPainting(new java.net.URI(fields(0)), fields(1).toInt, fields(2).toInt, "image/jpeg", None))
        }
      }
    )
  }

  val output = new File("example-manifest.json")
  if (output.exists) output.delete()

  val writer = RDFWriter[Rdf, RDFJson]

  import ops._

  writer.write(
    manifest.jsonResource.toPG.graph,
    new java.io.FileOutputStream(output),
    manifest.base.toString
  )
}

