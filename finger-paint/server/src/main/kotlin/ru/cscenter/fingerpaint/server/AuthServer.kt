package ru.cscenter.fingerpaint.server

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import spark.Request
import spark.Spark.before
import spark.Spark.halt

class AuthServer {

    companion object {
        const val CLIENT_ID = ""
        const val PASSWORD = ""
    }

    init {
        check(CLIENT_ID.isNotEmpty()) {
            "Server client id is not specified! Please refer to https://developers.google.com/identity/sign-in/android/start-integrating"
        }
        check(PASSWORD.isNotEmpty()) {
            "Password is not specified! Please add password for admin access"
        }
    }

    private val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), JacksonFactory())
        .setAudience(listOf(CLIENT_ID))
        .build()

    fun run() {
        before("${Server.public}/*") { request, _ ->
            try {
                val key = request.headers("access_key")
                val token = verifier.verify(key)
                if (token != null) {
                    val userId = token.payload.subject
                    request.attribute("id", userId)
                } else {
                    halt(401)
                }
            } catch (e: Exception) {
                println(e)
                halt(401)
            }

        }

        before("${Server.private}/*") { request, _ ->
            if (!checkAdminAccess(request)) {
                halt(401)
            }
        }
    }

    private fun checkAdminAccess(request: Request): Boolean {
        val key = request.headers("password")
        return key == PASSWORD
    }
}
