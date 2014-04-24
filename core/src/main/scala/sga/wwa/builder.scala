package edu.umd.mith.sga.wwa

import com.github.jsonldjava.utils.JsonUtils
import org.w3.banana._
import org.w3.banana.syntax._
import edu.umd.mith.banana.jena.DefaultGraphJenaModule
import edu.umd.mith.sga.model.SgaManifest
import edu.umd.mith.sga.json.IndexManifest
import edu.umd.mith.sga.rdf._
import edu.umd.mith.banana.io._
import edu.umd.mith.banana.jena.io._
import java.io.{ File, FileOutputStream, PrintWriter }

object DevelopmentBuilder extends Builder with App {
  val outputDir = new File(new File("output", "development"), "primary")

  trait Dev extends WwaConfiguration
    with DevelopmentConfiguration
    with BodleianImages
    with SgaTei { this: WwaManifest => }  
  
    save(new Duk00055Manifest with Dev, outputDir)
    save(new Duk00086Manifest with Dev, outputDir)
    save(new Duk00106Manifest with Dev, outputDir)
    save(new Duk00107Manifest with Dev, outputDir)
    save(new Duk00109Manifest with Dev, outputDir)
    save(new Duk00110Manifest with Dev, outputDir)
    save(new Duk00111Manifest with Dev, outputDir)
    save(new Duk00144Manifest with Dev, outputDir)
    // save(new Duk00169Manifest with Dev, outputDir)
    save(new Duk00171Manifest with Dev, outputDir)
    save(new Duk00172Manifest with Dev, outputDir)
    save(new Duk00175Manifest with Dev, outputDir)
    save(new Duk00176Manifest with Dev, outputDir)
    save(new Duk00177Manifest with Dev, outputDir)
    save(new Duk00178Manifest with Dev, outputDir)
    save(new Duk00179Manifest with Dev, outputDir)
    save(new Duk00180Manifest with Dev, outputDir)
    // save(new Duk00184Manifest with Dev, outputDir)
    save(new Duk00185Manifest with Dev, outputDir)
    // save(new Duk00188Manifest with Dev, outputDir)
    // save(new Duk00189Manifest with Dev, outputDir)
    save(new Duk00191Manifest with Dev, outputDir)
    // save(new Duk00192Manifest with Dev, outputDir)
    // save(new Duk00195Manifest with Dev, outputDir)
    save(new Duk00196Manifest with Dev, outputDir)
    save(new Duk00197Manifest with Dev, outputDir)
    // save(new Duk00198Manifest with Dev, outputDir)
    // save(new Duk00200Manifest with Dev, outputDir)
    // save(new Duk00201Manifest with Dev, outputDir)
    save(new Duk00673Manifest with Dev, outputDir)
    // save(new Duk00674Manifest with Dev, outputDir)
    save(new Duk00677Manifest with Dev, outputDir)
    save(new Duk00678Manifest with Dev, outputDir)
    save(new Duk00679Manifest with Dev, outputDir)
    // save(new Duk00681Manifest with Dev, outputDir)
    save(new Duk00682Manifest with Dev, outputDir)
    save(new Duk00683Manifest with Dev, outputDir)
    save(new Duk00684Manifest with Dev, outputDir)
    save(new Duk00685Manifest with Dev, outputDir)
    // save(new Duk00686Manifest with Dev, outputDir)
    save(new Duk00687Manifest with Dev, outputDir)
    // save(new Duk00692Manifest with Dev, outputDir)
    // save(new Duk00705Manifest with Dev, outputDir)
    // save(new Duk00707Manifest with Dev, outputDir)
    // save(new Duk03782Manifest with Dev, outputDir)
    save(new Loc03399Manifest with Dev, outputDir)
    save(new Loc03400Manifest with Dev, outputDir)
    save(new Loc03401Manifest with Dev, outputDir)
    save(new Loc03402Manifest with Dev, outputDir)
    save(new Loc03403Manifest with Dev, outputDir)
    save(new Loc03404Manifest with Dev, outputDir)
    save(new Loc03405Manifest with Dev, outputDir)
    save(new Loc03408Manifest with Dev, outputDir)
    save(new Loc03409Manifest with Dev, outputDir)
    save(new Loc03410Manifest with Dev, outputDir)
    save(new Loc03411Manifest with Dev, outputDir)
    save(new Loc03412Manifest with Dev, outputDir)
    save(new Loc03413Manifest with Dev, outputDir)
    save(new Loc03415Manifest with Dev, outputDir)
    save(new Loc03418Manifest with Dev, outputDir)
    save(new Loc03419Manifest with Dev, outputDir)
    // save(new Loc03420Manifest with Dev, outputDir)
    save(new Loc03421Manifest with Dev, outputDir)
    save(new Loc03423Manifest with Dev, outputDir)
    save(new Loc03424Manifest with Dev, outputDir)
    save(new Loc03425Manifest with Dev, outputDir)
    save(new Loc03426Manifest with Dev, outputDir)
    // save(new Loc03428Manifest with Dev, outputDir)
    save(new Loc03429Manifest with Dev, outputDir)
    // save(new Loc03430Manifest with Dev, outputDir)
    save(new Loc03434Manifest with Dev, outputDir)
    save(new Loc03436Manifest with Dev, outputDir)
    save(new Loc03437Manifest with Dev, outputDir)
    save(new Loc03440Manifest with Dev, outputDir)
    save(new Loc03441Manifest with Dev, outputDir)
    save(new Loc03442Manifest with Dev, outputDir)
    save(new Loc03445Manifest with Dev, outputDir)
    save(new Loc03450Manifest with Dev, outputDir)
    // save(new Loc03456Manifest with Dev, outputDir)
    // save(new Loc03784Manifest with Dev, outputDir)
    save(new Loc03801Manifest with Dev, outputDir)
    save(new Nyp00027Manifest with Dev, outputDir)
    // save(new Nyp00030Manifest with Dev, outputDir)
    // save(new Nyp00031Manifest with Dev, outputDir)
    save(new Nyp00035Manifest with Dev, outputDir)
    // save(new Nyp00392Manifest with Dev, outputDir)

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
