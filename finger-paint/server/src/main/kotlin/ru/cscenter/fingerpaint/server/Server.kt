package ru.cscenter.fingerpaint.server

import org.slf4j.LoggerFactory
import ru.cscenter.fingerpaint.api.*
import ru.cscenter.fingerpaint.db.Dao
import ru.cscenter.fingerpaint.db.entities.StatisticId
import spark.Request
import spark.Spark.*
import java.sql.Date
import java.time.ZoneId
import java.time.ZonedDateTime

class Server {
    private val loginServer = AuthServer()
    private val logger = LoggerFactory.getLogger(Server::class.java)

    companion object {
        private const val okResponse = "ok"
        private const val patients = "/patients"
        private const val statistics = "/statistics"
        private val timeZone = ZoneId.of("Europe/Moscow")

        private fun date() = ZonedDateTime.now(timeZone)
    }

    fun run() {
        Dao.init()

        loginServer.run()

        exception(Exception::class.java) { e, _, _ ->
            e.printStackTrace()
        }

        get("$patients/exists/:id") { request, _ ->
            val patientId = request.params("id").toLong()
            Dao.containsPatient(patientId)
        }

        get(patients) { request, _ ->
            val id = getId(request)
            val patients = Dao.selectPatients(id).map { it.toApiPatient() }
            toJsonArray(patients)
        }

        post(patients) { request, _ ->
            val id = getId(request)
            val patientsNames = getPatientNames(request).distinct()
            val patients = Dao.insertPatients(id, patientsNames).map { it.toApiPatient() }
            toJsonArray(patients)
        }

        delete("$patients/:id") { request, _ ->
            val patientId = request.params("id").toLong()
            Dao.deletePatients(listOf(patientId))
            okResponse
        }

        put(patients) { request, _ ->
            val patient = getPatient(request)
            Dao.renamePatient(patient.id, patient.name)
            okResponse
        }


        get("$statistics/:id") { request, _ ->
            val patientId = request.params("id").toLong()
            val statistics = Dao.selectStatistics(listOf(patientId)).map { it.toApiStatistic() }
            toJsonArray(statistics)
        }

        put(statistics) { request, _ ->
            val gameResult = getGameResult(request)
            val statisticId =
                StatisticId(gameResult.patientId, Date.valueOf(gameResult.date), gameResult.type)
            Dao.updateStatistic(statisticId, gameResult.success)
            okResponse
        }


        before("/*") { request, _ ->
            logger.info("${date()}\n\t>>>> ${request.requestMethod()} ${request.url()}\n\tbody: ${request.body()}")
        }

        after("/*") { request, response ->
            response.type("application/json")
            logger.info("${date()}\n\t<<<< ${request.requestMethod()} ${request.url()}\n\tbody: ${response.body()}")
        }
    }

    private fun getId(request: Request) = request.attribute<Long>("id")
    private fun getPatientNames(request: Request) =
        fromJsonArray<ApiPatientName>(request.body())!!.map { it.name }

    private fun getPatient(request: Request) = fromJson<ApiPatient>(request.body())!!
    private fun getGameResult(request: Request) = fromJson<ApiGameResult>(request.body())!!
}
