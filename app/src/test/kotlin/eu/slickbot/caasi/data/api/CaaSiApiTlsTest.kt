package eu.slickbot.caasi.data.api

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.net.ssl.SSLException

class CaaSiApiTlsTest {

  private lateinit var server: MockWebServer
  private lateinit var client: OkHttpClient

  @Before
  fun setUp() {
    val heldCert = HeldCertificate.Builder()
      .addSubjectAlternativeName("example.com")
      .build()
    val serverCerts = HandshakeCertificates.Builder()
      .heldCertificate(heldCert)
      .build()
    val clientCerts = HandshakeCertificates.Builder()
      .addTrustedCertificate(heldCert.certificate)
      .build()

    server = MockWebServer()
    server.useHttps(serverCerts.sslSocketFactory(), false)
    server.enqueue(MockResponse().setBody("ok"))
    server.start()

    client = OkHttpClient.Builder()
      .sslSocketFactory(clientCerts.sslSocketFactory(), clientCerts.trustManager)
      .build()
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun getBaseUrl_rejectsServerWhoseCertificateHostnameDoesNotMatch() {
    val api = CaaSiApi(client, moshi = Moshi.Builder().build(), baseIdUrl = server.url("/").toString())
    Assert.assertThrows(SSLException::class.java) {
      api.getBaseUrl()
    }
  }
}
