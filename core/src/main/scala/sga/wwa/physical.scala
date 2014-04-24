package edu.umd.mith.sga.wwa

import edu.umd.mith.scalanvas.model.Sequence
import edu.umd.mith.sga.model.SgaCanvas

trait PhysicalManifest extends WwaManifest {
  this: WwaConfiguration =>
  val ranges = Nil
  
  def pages: List[(String, (String, String))]

  private lazy val canvases = pages.map {
    case (fileId, (shelfmark, folio)) => parseTeiFile(fileId, shelfmark, folio)
  }

  lazy val sequence = Sequence[SgaCanvas](
    Some(itemBasePlus("/physical-sequence")),
    "Physical sequence",
    canvases
  )
}
