package edu.umd.mith.sga.frankenstein

import com.github.jsonldjava.utils.{ JSONUtils => JsonUtils }
//import argonaut._, Argonaut._
import org.w3.banana._
import org.w3.banana.syntax._
import edu.umd.mith.banana.jena.DefaultGraphJenaModule
import edu.umd.mith.sga.model.SgaManifest
import edu.umd.mith.sga.json.IndexManifest
import edu.umd.mith.sga.rdf._
//import edu.umd.mith.banana.argo._
import edu.umd.mith.banana.io._
import edu.umd.mith.banana.jena.io._
import java.io.{ File, FileOutputStream, PrintWriter }

object JsonLdDemoBuilder extends JsonLdBuilder with App {
  val outputDir = new File("jsonld-demo")

  trait Dev extends FrankensteinConfiguration
    with BodleianImages
    with SgaTei { this: FrankensteinManifest => }

  // save(new LessingManifest with Dev, outputDir)
}

trait JsonLdBuilder extends DefaultGraphJenaModule {
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

