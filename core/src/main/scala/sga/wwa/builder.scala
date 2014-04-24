package edu.umd.mith.sga.wwa

import com.github.jsonldjava.utils.{ JSONUtils => JsonUtils }
import org.w3.banana._
import org.w3.banana.syntax._
import edu.umd.mith.banana.jena.DefaultGraphJenaModule
import edu.umd.mith.sga.model.SgaManifest
import edu.umd.mith.sga.rdf._
import edu.umd.mith.sga.wwa.util.ShelfmarkMapReader
import edu.umd.mith.banana.io._
import edu.umd.mith.banana.jena.io._
import java.io.{ File, FileOutputStream }

object DevelopmentBuilder extends Builder with ShelfmarkMapReader with App {
  val outputDir = new File(new File("output", "development"), "primary")

  def shelfmarkMapFile = new File(args(0))

  trait Dev extends WwaConfiguration
    with DevelopmentConfiguration
    with BodleianImages
    with SgaTei { this: WwaManifest => }

  shelfmarkMap.groupBy(_._1.split("-").head).toList.sortBy(_._1).foreach {
    case (notebookId, notebookPages) =>
      val manifest = new PhysicalManifest with Dev {
        val id = notebookId
        val pages = notebookPages
      }

      try {
        save(manifest, outputDir)
      } catch {
        case e: Exception =>
          println(s"Warning: unable to create manifest for $notebookId.")
      }
  }
}

trait Builder extends DefaultGraphJenaModule {
  def save(manifest: SgaManifest, outputDir: File) = {
    import Ops._

    val dir = new File(outputDir, manifest.id)
    dir.mkdirs

    val output = new File(dir, "Manifest.jsonld")
    if (output.exists) output.delete()

    val writer = new JsonLdWriter[java.util.Map[String, Object]] {
      val context = JsonUtils.fromString(
        io.Source.fromInputStream(
          getClass.getResourceAsStream("/edu/umd/mith/scalanvas/context.json")
        ).mkString
      ).asInstanceOf[java.util.Map[String, Object]]
    }

    writer.write(
      manifest.jsonResource.toPG.graph,
      new FileOutputStream(output),
      manifest.base.toString
    )
  }
}
