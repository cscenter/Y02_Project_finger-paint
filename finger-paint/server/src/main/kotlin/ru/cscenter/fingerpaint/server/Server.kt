package ru.cscenter.fingerpaint.server

import org.slf4j.LoggerFactory
import ru.cscenter.fingerpaint.api.toJsonArray
import ru.cscenter.fingerpaint.db.Dao
import spark.Request
import spark.Spark.*

class Server {
    private val loginServer = AuthServer()
    private val logger = LoggerFactory.getLogger(Server::class.java)

    fun run() {
        port(8080)
        loginServer.run()

        exception(Exception::class.java) { e, _, _ ->
            e.printStackTrace()
        }

        get("/patients") { request, _ ->
            val id = getId(request)
            val patients = Dao.selectPatients(id).map { it.toApiPatient() }
            toJsonArray(patients)
        }

        before("/*") { request, _ ->
            logger.info("Request ${request.requestMethod()} ${request.url()}\nbody: ${request.body()}")
        }

        after("/*") { request, response ->
            response.type("application/json")
            logger.info("Response ${request.requestMethod()} ${request.url()}\nbody: ${response.body()}")
        }
    }

    private fun getId(request: Request) = request.attribute<Long>("id")
}
