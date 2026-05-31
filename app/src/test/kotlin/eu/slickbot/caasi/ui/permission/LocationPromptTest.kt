package eu.slickbot.caasi.ui.permission

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationPromptTest {

  @Test
  fun permanentlyDeniedTap_offersAppSettings() {
    val prompt = nextLocationPrompt(granted = false, shouldShowRationale = false)
    assertEquals(LocationPrompt.OfferSettings, prompt)
  }

  @Test
  fun grantedTap_returnsNull() {
    val prompt = nextLocationPrompt(granted = true, shouldShowRationale = false)
    assertEquals(null, prompt)
  }

  @Test
  fun deniedOnceTap_showsRationaleBeforeRetrying() {
    val prompt = nextLocationPrompt(granted = false, shouldShowRationale = true)
    assertEquals(LocationPrompt.RequestWithRationale, prompt)
  }

  @Test
  fun coarseOnlyAccess_offersPreciseUpgrade() {
    val offer = shouldOfferPreciseUpgrade(
      hasLocationAccess = true,
      hasPreciseLocation = false,
      alreadyOffered = false,
    )
    assertTrue(offer)
  }

  @Test
  fun preciseAlreadyGranted_doesNotOfferUpgrade() {
    val offer = shouldOfferPreciseUpgrade(
      hasLocationAccess = true,
      hasPreciseLocation = true,
      alreadyOffered = false,
    )
    assertFalse(offer)
  }

  @Test
  fun alreadyOfferedThisSession_doesNotOfferUpgradeAgain() {
    val offer = shouldOfferPreciseUpgrade(
      hasLocationAccess = true,
      hasPreciseLocation = false,
      alreadyOffered = true,
    )
    assertFalse(offer)
  }

  @Test
  fun noLocationAccess_doesNotOfferPreciseUpgrade() {
    val offer = shouldOfferPreciseUpgrade(
      hasLocationAccess = false,
      hasPreciseLocation = false,
      alreadyOffered = false,
    )
    assertFalse(offer)
  }
}
