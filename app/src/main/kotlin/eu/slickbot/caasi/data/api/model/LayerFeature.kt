package eu.slickbot.caasi.data.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LayerFeature(
  val id: Int,
  val geometry: Geometry,
  val type: String, // always "Feature"
//  val properties: Properties,
  val properties: Map<String, String>,
) {

  val fid = firstProperty("OBJECTID", "FID")

  val nameSi = firstProperty("IME_ZNAMEN", "IME", "Naziv", "NAZIV", "letalisce", "Heliport", "Drzava", "OBMOCJE")
  val nameEn = firstProperty("subject", "Subject", "SUBJECT", "Zone")

  val notamSi = firstProperty("glej_NOTAM")
  val notamEn = firstProperty("Check_NOTAM")

  val restrictionsSi = firstProperty("UAS_omejitve", "Omejitev", "OMEJITEV", "omejitev", "UAS_omejit")
  val restrictionsEn = firstProperty("UAS_restriction", "UAS_restrictions", "Restrict_", "RESTRICT_", "RESTRICTIO", "restriction", "UAS_restri")

  val regulationSi = firstProperty("Zakonodaja", "zakonodaja", "ZAKON")
  val regulationEn = firstProperty("Regulation", "regulation", "REGUL")

  val reasonSi = firstProperty("Razlog", "razlog", "RAZLOG")
  val reasonEn = firstProperty("Reason", "reason", "REASON")

  val openExceptionsSi = firstProperty("Izjema", "izjema", "izjeme", "IZJEMA", "IZJEME", "Izjeme_UAS_odprta", "Izjeme_odp", "Izjeme_odprta_posebna", "Izjeme_odprta_posebna_kat")
  val openExceptionsEn = firstProperty("Exception", "Exceptions", "exceptions", "EXCEPTION", "EXCEPTIONS", "exception_UAS_open", "Exceptions_open_specific", "Exceptions_open_specific_cat")

  val specificExceptionsSi = firstProperty("Izjeme_posebna", "Izjeme_za", "Izjeme_sam", "Izjeme_odprta_posebna", "Izjeme_odprta_posebna_kat")
  val specificExceptionsEn = firstProperty("Exceptions_specific", "Exception_UAS_specific", "Exception_specific_only", "Exceptions_specific_only", "Exceptions_open_specific", "Exceptions_open_specific_cat")

  val infoSi = firstProperty("Informacije", "Kontakt_Co")
  val infoEn = firstProperty("Information", "Kontakt_Co")

  val remarkSi = firstProperty("Opomba")
  val remarkEn = firstProperty("Remark")

  private fun firstProperty(vararg names: String, trim: Boolean = true): String? {
    return names
      .first { properties[it] != null }
      .let { if (trim) it.trim() else it }
  }

}
