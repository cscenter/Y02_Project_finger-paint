package ru.cscenter.fingerpaint.server

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import spark.Spark.before
import spark.Spark.halt

class AuthServer {

    companion object {
        const val CLIENT_ID = ""
    }

    private val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), JacksonFactory())
        .setAudience(listOf(CLIENT_ID))
        .build()

    fun run() {
        before("/*") { request, _ ->
            val key = request.headers("access_key")
            val token = verifier.verify(key)
            if (token != null) {
                val userId = token.payload.subject
                request.attribute("id", userId)
            } else {
                halt(401)
            }
        }
    }
}
