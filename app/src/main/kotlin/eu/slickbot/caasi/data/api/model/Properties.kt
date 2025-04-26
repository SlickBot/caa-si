package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Properties(

  // IDs = [ 0,1,2 ]
  @Json(name = "OBJECTID") // FID
  val objectId: Int?,

  // IDs = [ 0 ]
  @Json(name = "IME_ZNAMEN")
  val name: String?,
  // IDs = [ 0,2,3,4 ]
  @Json(name = "Subject")
  val subject: String?,

  // IDs = [ 0 ]
  @Json(name = "UAS_omejitve")
  val uasRestrictionSi: String?,
  // IDs = [ 0 ]
  @Json(name = "UAS_restriction")
  val uasRestrictionEn: String?,

  // IDs = [ 0 ]
  @Json(name = "Izjeme_odprta_posebna_kat")
  val exceptionsOpenSpecificCatSi: String?,
  // IDs = [ 0 ]
  @Json(name = "Exceptions_open_specific_cat")
  val exceptionsOpenSpecificCatEn: String?,

  // IDs = [ 0 ]
  @Json(name = "Informacije")
  val informationSi: String?,
  // IDs = [ 0 ]
  @Json(name = "Information")
  val informationEn: String?,

  // IDs = [ 3,4 ]
  @Json(name = "FID")
  val FID: String?,

  // IDs = [ 1 ]
  @Json(name = "IME")
  val IME: String?,
  // IDs = [ 1 ]
  @Json(name = "glej_NOTAM")
  val NOTAM_SL: String?,
  // IDs = [ 1 ]
  @Json(name = "Check_NOTAM")
  val NOTAM_EN: String?,

  // IDs = [ 2 ]
  @Json(name = "letalisce")
  val letalisce: String?,

  // IDs = [ 2 ]
  @Json(name = "kontakt")
  val kontakt: String?,

  // IDs = [ 2 ]
  @Json(name = "Izjeme_odprta_posebna")
  val exceptionsOpenSpecificSi: String?,
  // IDs = [ 2 ]
  @Json(name = "Exceptions_open_specific")
  val exceptionsOpenSpecificEn: String?,

  // IDs = [ 2,3,4 ]
  @Json(name = "Naziv")
  val Naziv: String?,

  // IDs = [ 3,4 ]
  @Json(name = "Omejitev")
  val Omejitev: String?,
  // IDs = [ 3,4 ]
  @Json(name = "Restrict_")
  val Restriction: String?,
  // IDs = [ 3,4 ]
  @Json(name = "Izjema")
  val Izjema: String?,
  // IDs = [ 3,4 ]
  @Json(name = "Exception")
  val Exception: String?,
  // IDs = [ 3,4 ]
  @Json(name = "Zakonodaja")
  val Zakonodaja: String?,
  // IDs = [ 3,4 ]
  @Json(name = "Regulation")
  val Regulation: String?,
  // IDs = [ 3,4 ]
  @Json(name = "Razlog")
  val Razlog: String?,
  // IDs = [ 3,4 ]
  @Json(name = "Reason")
  val Reason: String?,

  @Json(name = "NAZIV")
  val NAZIV: String?,
  @Json(name = "SUBJECT")
  val SUBJECT: String?,
  @Json(name = "OMEJITEV")
  val OMEJITEV: String?,
  @Json(name = "RESTRICT_")
  val RESTRICTION: String?,
  @Json(name = "IZJEME")
  val IZJEME: String?,
  @Json(name = "EXCEPTIONS")
  val EXCEPTIONS: String?,

  // all
  @Json(name = "Shape__Area")
  val shapeArea: Double?,
  @Json(name = "Shape__Length")
  val shapeLength: Double?,
)
