package ru.cscenter.fingerpaint.server

import spark.Spark.before
import spark.Spark.halt

class AuthServer {
    private val keys = HashMap<String, Long>()

    init {
        keys["123"] = 1L
    }

    fun run() {
        before("/*") { request, _ ->
            val key = request.headers("access_key")
            if (keys.containsKey(key)) {
                val id = keys[key]
                request.attribute("id", id)
            } else {
                halt(401)
            }
        }
    }
}
