package eu.slickbot.caasi.data.api

import org.maplibre.android.geometry.LatLngBounds
import com.squareup.moshi.Moshi
import eu.slickbot.caasi.API_BASE_URL
import eu.slickbot.caasi.API_ID_URL
import eu.slickbot.caasi.data.api.http.HttpException
import eu.slickbot.caasi.data.api.model.Layer
import eu.slickbot.caasi.data.api.model.LayerFeature
import eu.slickbot.caasi.data.api.model.LayerFeaturesResponse
import eu.slickbot.caasi.data.api.model.LayersResponse
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.net.URLEncoder
import javax.net.ssl.HostnameVerifier

class CaaSiApi(
  client: OkHttpClient,
  private val moshi: Moshi,
  private val baseIdUrl: String = API_ID_URL,
  private val apiBaseUrl: String = API_BASE_URL,
) {

  companion object {
    const val DEFAULT_BASE_ID = "25ba69037c264c5faa5381174f76f861"
    const val DEFAULT_ITEM_ID = "14c35a6c4d5a472ea2ed5b2a75abc690"
    private const val BASE_ID_CERT_IDENTITY = "freedns.si"
  }

  private val client = client.withScopedHostnameVerifier(baseIdUrl)

  fun getBaseUrl(): HttpUrl {
    return request(baseIdUrl).use { it.request.url }
  }

  fun getBaseId(url: HttpUrl = getBaseUrl()): String {
    return url.queryParameter("id")!!
  }

  fun getItemId(baseId: String = getBaseId()): String {
    val data = getData(baseId)
    // { "map": { "itemId": "123456", ... }, ... }
    val obj = data.parseJson<Map<*, *>>()
    val map = obj["map"] as Map<*, *>
    return map["itemId"]!!.toString()
  }

  fun getData(baseId: String = getBaseId()): String {
    return requestString("$apiBaseUrl/content/items/$baseId/data?f=json")
  }

  fun getLayersResponse(itemId: String = getItemId()): LayersResponse {
    return requestString("$apiBaseUrl/content/items/$itemId/data?f=json").parseJson()
  }

  fun getLayers(itemId: String = getItemId()): List<Layer> {
    return getLayersResponse(itemId).operationalLayers
      .filter { it.url != null }
  }

    fun getLayerFeaturesResponse(
      layer: Layer,
      bounds: LatLngBounds? = null,
    ): LayerFeaturesResponse {
      val layerUrl = requireNotNull(layer.url) { "Layer '${layer.title}' has no URL" }
      //
      // OperationalLayer.url looks something like this:
      // https://services6.arcgis.com/1F2lR3M9nMUYDj5l/arcgis/rest/services/UAS_GEO_ZONES___March_2022_WFL1/FeatureServer/12
      //
      // append query params:
      // .../FeatureServer/12/query?f=geojson&where=1%3D1&returnGeometry=true&spatialRel=esriSpatialRelIntersects&outFields=*&maxRecordCountFactor=2&outSR=102100&resultOffset=0&resultRecordCount=4000
      // coordinates are in EPSG:3857, transform them into EPSG:4326
      //
      // OR
      //
      // .../FeatureServer/9/query?f=geojson&where=1%3D1&returnGeometry=true&spatialRel=esriSpatialRelIntersects&outFields=*&maxRecordCountFactor=200&resultRecordCount=400000
      // coordinates are in already in EPSG:4326
      //

      // val jsonString = requestString("${layer.url}/query?f=geojson&where=1%3D1&returnGeometry=true&spatialRel=esriSpatialRelIntersects&outFields=*&maxRecordCountFactor=200&resultRecordCount=400000&quantizationParameters={\"mode\":\"view\",\"originPosition\":\"upperLeft\",\"tolerance\":1.0583354500042332,\"extent\":{\"xmin\":1510518.929297328,\"ymin\":5696339.608744907,\"xmax\":1840386.3517350955,\"ymax\":5892155.961049096,\"spatialReference\":{\"wkid\":4326,\"latestWkid\":4326}}}")

      val url = if (bounds != null) {
        createUrl(
          "${layerUrl}/query",
          "f" to "geojson",
          "returnGeometry" to true,
          "spatialRel" to "esriSpatialRelIntersects",
          "geometryType" to "esriGeometryEnvelope",
          "inSR" to 4326,
          "outSR" to 4326,
          "outFields" to "*",
          "returnCentroid" to false,
          "resultRecordCount" to 5000,
          "maxRecordCountFactor" to 3,
          "geometry" to jsonString(
            "xmin" to bounds.southWest.longitude,
            "ymin" to bounds.southWest.latitude,
            "xmax" to bounds.northEast.longitude,
            "ymax" to bounds.northEast.latitude,
            "spatialReference" to mapOf(
              "wkid" to 4326
            )
          ),
          "quantizationParameters" to jsonString(
            "mode" to "view",
            "originPosition" to "upperLeft",
            "extent" to mapOf(
              "xmin" to bounds.southWest.longitude,
              "ymin" to bounds.southWest.latitude,
              "xmax" to bounds.northEast.longitude,
              "ymax" to bounds.northEast.latitude,
              "spatialReference" to mapOf(
                "wkid" to 4326
              )
            )
          )
        )
      } else {
        createUrl(
          "${layerUrl}/query",
          "f" to "geojson",
          "where" to "1=1",
          "returnGeometry" to true,
          "spatialRel" to "esriSpatialRelIntersects",
          "inSR" to 4326,
          "outSR" to 4326,
          "outFields" to "*",
          "resultRecordCount" to 1000,
          "maxRecordCountFactor" to 200,
        )
      }
      return requestString(url).parseJson()
    }

  fun getLayerFeatures(layer: Layer, bounds: LatLngBounds? = null): List<LayerFeature> {
    return getLayerFeaturesResponse(layer, bounds).features
  }

  /*
   * Helpers
   */

  private fun createUrl(baseUrl: String, vararg params: Pair<String, Any>): String {
    return buildString {
      append(baseUrl.removeSuffix("/"))
      append("?")
      append(params.joinToString("&") {
        "${it.first.urlEncode()}=${it.second.toString().urlEncode()}"
      })
    }
  }

  private fun String.urlEncode(): String {
    return URLEncoder.encode(this, "utf-8")
  }

  private inline fun <reified T : Any> String.parseJson(): T {
    val adapter = moshi.adapter(T::class.java)
    return adapter.fromJson(this)!!
  }

  private fun request(url: String): Response {
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()
    if (!response.isSuccessful) {
      response.close()
      throw HttpException(response.code, url)
    }
    return response
  }

  private fun requestString(url: String): String {
    return request(url).use { it.body.string() }
  }

  private fun jsonString(vararg pairs: Pair<String, Any>): String {
    return pairs.toMap().toJsonObject().toString()
  }

  private fun Map<String, Any>.toJsonObject(): JSONObject {
    return JSONObject(this)
  }

  private fun OkHttpClient.withScopedHostnameVerifier(baseIdUrl: String): OkHttpClient {
    val baseIdHost = baseIdUrl.toHttpUrlOrNull()?.host ?: return this
    val default = hostnameVerifier
    val scoped = HostnameVerifier { hostname, session ->
      val identityToCheck = if (hostname == baseIdHost) BASE_ID_CERT_IDENTITY else hostname
      default.verify(identityToCheck, session)
    }
    return newBuilder()
      .hostnameVerifier(scoped)
      .build()
  }
}
