package eu.slickbot.caasi.ui.permission

sealed class LocationPrompt {
  /** Permission state allows another system request; show a rationale dialog first. */
  data object RequestWithRationale : LocationPrompt()

  /**
   * System won't show the permission dialog anymore (user permanently denied);
   * offer to open app settings as the only recovery path.
   */
  data object OfferSettings : LocationPrompt()
}

/**
 * Decides what to do when the user taps the location FAB and permission isn't granted.
 *
 * Returns null when permission is granted (caller should just use the location).
 *
 * Assumes the app has already attempted at least one auto-request, so
 * `shouldShowRationale == false` is interpreted as "permanently denied"
 * rather than "never asked".
 */
fun nextLocationPrompt(
  granted: Boolean,
  shouldShowRationale: Boolean,
): LocationPrompt? = when {
  granted -> null
  shouldShowRationale -> LocationPrompt.RequestWithRationale
  else -> LocationPrompt.OfferSettings
}

/**
 * Decides whether to nudge the user to upgrade from approximate to precise
 * location after the location FAB is tapped.
 *
 * Only fires when the app already has (coarse) access but lacks precise
 * location, and only if the nudge hasn't already been offered this session —
 * approximate location still works, so this is an optional one-time prompt
 * rather than a gate.
 */
fun shouldOfferPreciseUpgrade(
  hasLocationAccess: Boolean,
  hasPreciseLocation: Boolean,
  alreadyOffered: Boolean,
): Boolean {
  return hasLocationAccess && !hasPreciseLocation && !alreadyOffered
}
