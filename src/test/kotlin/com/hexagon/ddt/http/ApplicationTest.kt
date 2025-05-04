package com.hexagon.ddt.http

import java.util.*
import com.hexagon.domain.application.Routes
import com.hexagon.ddt.BaseTest
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplicationTest: BaseTest() {

    val app = Routes().resources

    @Test
    fun `test create account`() {
        val accountId = UUID.randomUUID()

        val request = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        val response = app(request)

        assertThat(response, hasStatus(Status.CREATED))
        assertEquals(response.bodyString(), "Account created")
    }

    @Test
    fun `test deposit money`() {
        val accountId = UUID.randomUUID()

        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        app(createRequest)

        // Then, deposit money
        val depositRequest = Request(POST, "/accounts/deposit").body("""{"accountId":"$accountId","amount":50.0}""")
        val response = app(depositRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "Deposit successful")
    }

    @Test
    fun `test withdraw money`() {
        val accountId = UUID.randomUUID()

        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        app(createRequest)

        // Then, withdraw money
        val withdrawRequest = Request(POST, "/accounts/withdraw").body("""{"accountId":"$accountId","amount":30.0}""")
        val response = app(withdrawRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "Withdrawal successful")
    }

    @Test
    fun `test get balance`() {
        val accountId = UUID.randomUUID()
        // First, create the account
        val createRequest = Request(POST, "/accounts").body("""{"accountId":"$accountId","initialBalance":100.0}""")
        app(createRequest)

        // Then, deposit money
        val depositRequest = Request(POST, "/accounts/deposit").body("""{"accountId":"$accountId","amount":50.0}""")
        app(depositRequest)

        // Finally, get the balance
        val balanceRequest = Request(GET, "/accounts/$accountId/balance")
        val response = app(balanceRequest)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(response.bodyString(), "150.0")
    }
}
