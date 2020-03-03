package ru.cscenter.fingerpaint.server

import org.slf4j.LoggerFactory
import ru.cscenter.fingerpaint.api.*
import ru.cscenter.fingerpaint.db.Dao
import ru.cscenter.fingerpaint.db.entities.Statistic
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
        private const val login = "/login"
        private val timeZone = ZoneId.of("Europe/Moscow")

        private fun date() = ZonedDateTime.now(timeZone)
    }

    fun run() {
        Dao.init()

        loginServer.run()

        exception(Exception::class.java) { e, _, _ ->
            e.printStackTrace()
        }

        post(login) { request, _ ->
            val id = getId(request)
            Dao.insertUser(id)
            okResponse
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

        post("$patients/new") { request, _ ->
            val id = getId(request)
            val newUserStatistics = getNewUsersStatistics(request).associate {
                Pair(it.name, it.statistics.map { statistic ->
                    Statistic(
                        StatisticId(0, Date.valueOf(statistic.date), statistic.type),
                        statistic.total,
                        statistic.success
                    )
                })
            }

            Dao.insertPatientsWithStatistics(id, newUserStatistics)
            okResponse
        }

        delete("$patients/:id") { request, _ ->
            val patientId = request.params("id").toLong()
            Dao.deletePatients(listOf(patientId))
            okResponse
        }

        put(patients) { request, _ ->
            val patients = getPatients(request)
            for (patient in patients) {
                Dao.renamePatient(patient.id, patient.name)
            }
            okResponse
        }

        // TODO use ApiUserStatistics
        get("$statistics/:id") { request, _ ->
            val patientId = request.params("id").toLong()
            val statistics = Dao.selectStatistics(listOf(patientId)).map { it.toApiStatistic() }
            toJsonArray(statistics)
        }

        // TODO create request for all patients, use ApiUserStatistics
        // get(statistics)

        put(statistics) { request, _ ->
            val gameResults = getGameResults(request)
            for (gameResult in gameResults) {
                val statisticId =
                    StatisticId(gameResult.patientId, Date.valueOf(gameResult.date), gameResult.type)
                Dao.updateStatistic(statisticId, gameResult.success)
            }
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

    private fun getId(request: Request) = request.attribute<String>("id")
    private fun getPatientNames(request: Request) =
        fromJsonArray<ApiPatientName>(request.body())!!.map { it.name }

    private fun getPatients(request: Request) = fromJsonArray<ApiPatient>(request.body())!!
    private fun getGameResults(request: Request) = fromJsonArray<ApiGameResult>(request.body())!!
    private fun getNewUsersStatistics(request: Request) =
        fromJsonArray<ApiNewPatientStatistics>(request.body())!!
}
