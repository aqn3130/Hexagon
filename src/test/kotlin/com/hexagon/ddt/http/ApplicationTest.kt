package com.hexagon.ddt.http

import java.util.*
import com.hexagon.ddt.BaseTest
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.filter.ClientFilters
import org.http4k.hamkrest.hasStatus
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ApplicationTest: BaseTest() {


    private lateinit var client: HttpHandler
    private var server: org.http4k.server.Http4kServer? = null


    @BeforeEach
    fun setup() {
        val mode = System.getenv("testMode") ?: "IN_MEMORY"
        when (mode.uppercase()) {
            "IN_MEMORY" -> client = app
            "EMBEDDED_HTTP" -> {
                server = app.asServer(Undertow(0)).start()
                val baseUri = "http://localhost:${server!!.port()}"
                client = ApacheClient()
                client = ClientFilters.SetBaseUriFrom(Uri.of(baseUri)).then(ApacheClient())

            }
        }
    }

    @AfterEach
    fun teardown() {
        server?.stop()
    }

    @Test
    fun `test create account`() {
        val accountId = UUID.randomUUID()
        val request = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        request.header("Content-Type", "application/json")
        val response = client(request)

        assertThat(response, hasStatus(Status.CREATED))
        assertEquals(response.bodyString(), "Account created")
    }

    @Test
    fun `test deposit money`() {
        val accountId = UUID.randomUUID()

        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        client(createRequest)

        // Then, deposit money
        val depositRequest = Request(POST, "/accounts/deposit").body("""{"accountId":"$accountId","amount":50.0}""")
        val response = client(depositRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "Deposit successful")
    }

    @Test
    fun `test withdraw money`() {
        val accountId = UUID.randomUUID()

        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        client(createRequest)

        // Then, withdraw money
        val withdrawRequest = Request(POST, "/accounts/withdraw").body("""{"accountId":"$accountId","amount":30.0}""")
        val response = client(withdrawRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "Withdrawal successful")
    }

    @Test
    fun `test get balance`() {
        val accountId = UUID.randomUUID()
        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        client(createRequest)

        // Then, deposit money
        val depositRequest = Request(POST, "/accounts/deposit").body("""{"accountId":"$accountId","amount":50.0}""")
        client(depositRequest)

        // Finally, get the balance
        val balanceRequest = Request(GET, "/accounts/$accountId/balance")
        val response = client(balanceRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "150.0")
    }
}
