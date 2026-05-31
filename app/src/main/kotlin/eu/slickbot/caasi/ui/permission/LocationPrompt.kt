package eu.slickbot.caasi.ui.permission

sealed class LocationPrompt {
  data object RequestWithRationale : LocationPrompt()
  data object OfferSettings : LocationPrompt()
}

fun nextLocationPrompt(
  granted: Boolean,
  shouldShowRationale: Boolean,
): LocationPrompt? = when {
  granted -> null
  shouldShowRationale -> LocationPrompt.RequestWithRationale
  else -> LocationPrompt.OfferSettings
}

fun shouldOfferPreciseUpgrade(
  hasLocationAccess: Boolean,
  hasPreciseLocation: Boolean,
  alreadyOffered: Boolean,
): Boolean {
  return hasLocationAccess && !hasPreciseLocation && !alreadyOffered
}
