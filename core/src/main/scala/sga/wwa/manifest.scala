package edu.umd.mith.sga.wwa

import edu.umd.mith.scalanvas.model.Service
import edu.umd.mith.sga.model.SgaManifest

import java.io.File
import java.net.URI

trait WwaManifest extends SgaManifest with TeiManager {
  this: WwaConfiguration =>

  def resolvableDomain = "%sshelleygodwinarchive.org".format(
    if (development) "dev." else ""
  )

  def domain = "shelleygodwinarchive.org" 
  
  def base = new URI(
    "http://%s/data/ox".format(domain) 
  )

  val ServicePattern = """(duk|loc|mid|nyp)\.(\d+)""".r

  def service = Some(
    Service(
      id match {
        case ServicePattern(group, item) =>
          new URI(
            "http://%s/sc/oxford/wwa/%s/%s".format(
              resolvableDomain,
              group,
              item
            )
          )
      }
    )
  )

  val title = "Wwa"

  def label = id match {
    case "duk.00055" => "Lessing's Laocoön."
    case _ => "Some work"
  }

  override val state = Some("...")

  override def date = Some(
    id match {
      case id => "Some date"
    }
  )

  import WwaManifest.IdWithSeq

  override val agent = Some("...")
  override val attribution = Some("...")
}

object WwaManifest {
  val IdWithSeq = """(duk|loc|mid|nyp)\.([^-]+)-(\d\d\d\d)""".r
}

