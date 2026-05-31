package eu.slickbot.caasi.data.api

import com.squareup.moshi.Moshi
import eu.slickbot.caasi.LiveNetwork
import eu.slickbot.caasi.data.api.http.OkHttpClientBuilder
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(LiveNetwork::class)
class CaaSiApiLiveTest {

  private lateinit var api: CaaSiApi

  @Before
  fun setUp() {
    api = CaaSiApi(
      client = OkHttpClientBuilder.build(enableHttpLogging = false),
      moshi = Moshi.Builder().build(),
    )
  }

  @Test
  fun live_resolvesBaseAndItemId() {
    val baseId = api.getBaseId()
    assertTrue("baseId should be non-blank", baseId.isNotBlank())
    val itemId = api.getItemId(baseId)
    assertTrue("itemId should be non-blank", itemId.isNotBlank())
    println("baseId=$baseId itemId=$itemId")
  }

  @Test
  fun live_fetchesOperationalLayers() {
    val layers = api.getLayers()
    assertTrue("expected at least one operational layer with a url", layers.isNotEmpty())
    layers.forEach { println("layer ${it.id} '${it.title}' -> ${it.url}") }
  }

  @Test
  fun live_fetchesAndParsesFeaturesForFirstLayer() {
    val layers = api.getLayers()
    assertTrue("expected at least one layer", layers.isNotEmpty())
    val layer = layers.first()
    val features = api.getLayerFeatures(layer)
    println("layer '${layer.title}' returned ${features.size} features")
  }
}
