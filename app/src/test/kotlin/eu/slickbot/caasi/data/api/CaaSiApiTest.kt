package eu.slickbot.caasi.data.api

import com.squareup.moshi.Moshi
import eu.slickbot.caasi.data.api.http.OkHttpClientBuilder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CaaSiApiTest {

  private lateinit var server: MockWebServer
  private lateinit var api: CaaSiApi

  @Before
  fun setUp() {
    server = MockWebServer()
    server.start()
    val baseUrl = server.url("/").toString().trimEnd('/')
    api = CaaSiApi(
      client = OkHttpClientBuilder.build(enableHttpLogging = false),
      moshi = Moshi.Builder().build(),
      baseIdUrl = baseUrl,
      apiBaseUrl = baseUrl,
    )
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  private fun enqueueIdRedirect(id: String) {
    server.enqueue(
      MockResponse()
        .setResponseCode(301)
        .addHeader("Location", server.url("/apps/webappviewer/index.html?id=$id").toString())
    )
    server.enqueue(MockResponse().setBody(""))
  }

  private fun enqueueData(itemId: String) {
    server.enqueue(MockResponse().setBody("""{"map":{"itemId":"$itemId"}}"""))
  }

  private fun enqueueLayersResponse() {
    server.enqueue(MockResponse().setBody(LAYERS_JSON))
  }

  @Test
  fun is_base_id_same() {
    enqueueIdRedirect(CaaSiApi.DEFAULT_BASE_ID)
    assertEquals(CaaSiApi.DEFAULT_BASE_ID, api.getBaseId())
  }

  @Test
  fun is_item_id_same() {
    enqueueData(CaaSiApi.DEFAULT_ITEM_ID)
    assertEquals(CaaSiApi.DEFAULT_ITEM_ID, api.getItemId(baseId = "ignored"))
  }

  @Test
  fun can_get_baseUrl() {
    enqueueIdRedirect("abc123")
    val url = api.getBaseUrl()
    assertEquals("abc123", url.queryParameter("id"))
  }

  @Test
  fun can_get_baseId() {
    enqueueIdRedirect("anything-here")
    assertEquals("anything-here", api.getBaseId())
  }

  @Test
  fun can_get_itemId() {
    enqueueData("item-xyz")
    assertEquals("item-xyz", api.getItemId(baseId = "ignored"))
  }

  @Test
  fun can_get_data() {
    val body = """{"map":{"itemId":"x"}}"""
    server.enqueue(MockResponse().setBody(body))
    assertEquals(body, api.getData(baseId = "ignored"))
  }

  @Test
  fun can_get_layersResponse() {
    enqueueLayersResponse()
    val resp = api.getLayersResponse(itemId = "ignored")
    assertNotNull(resp)
    assertTrue(resp.operationalLayers.isNotEmpty())
  }

  @Test
  fun can_get_layers() {
    enqueueLayersResponse()
    val layers = api.getLayers(itemId = "ignored")
    // Fixture has two operationalLayers: one with url, one without.
    // getLayers must drop the url-less one.
    assertEquals(1, layers.size)
    assertEquals("with-url", layers[0].id)
  }

  companion object {
    private val LAYERS_JSON = """
      {
        "authoringApp": "WebAppBuilder",
        "authoringAppVersion": "2.23",
        "baseMap": {
          "baseMapLayers": [
            {
              "blendMode": "normal",
              "id": "base-1",
              "layerType": "ArcGISTiledMapServiceLayer",
              "opacity": 1,
              "title": "Base",
              "url": "https://example.com/base"
            }
          ],
          "title": "Topographic"
        },
        "bookmarks": [],
        "initialState": {
          "viewpoint": {
            "rotation": 0,
            "scale": 1000000.0,
            "targetGeometry": {
              "spatialReference": { "latestWkid": 3857, "wkid": 102100 },
              "xmax": 1.0, "xmin": 0.0, "ymax": 1.0, "ymin": 0.0
            }
          }
        },
        "operationalLayers": [
          {
            "id": "with-url",
            "title": "L1",
            "url": "https://example.com/layer1",
            "layerType": "FeatureLayer"
          },
          {
            "id": "no-url",
            "title": "L2",
            "layerType": "GroupLayer"
          }
        ],
        "spatialReference": { "latestWkid": 3857, "wkid": 102100 },
        "version": "2.23"
      }
    """.trimIndent()
  }
}
