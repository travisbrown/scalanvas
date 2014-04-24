package edu.umd.mith.sga.wwa.util

import java.io.File
import scala.io.Source

trait ShelfmarkMapReader {
  def shelfmarkMapFile: File

  private val Line = """^(duk|loc|mid|nyp)(\.[^-]+-\d\d\d\d)\s+([^,]+),\s(.+)$""".r

  lazy val shelfmarkMap: List[(String, (String, String))] = {
    val source = Source.fromFile(shelfmarkMapFile)

    val m = source.getLines.map {
      case Line(linePref, lineId, shelfmark, leaf) => linePref + lineId -> (shelfmark, leaf)
    }.toList

    source.close()

    m
  }
}

