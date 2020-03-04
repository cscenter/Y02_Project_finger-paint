package ru.cscenter.fingerpaint.server

import org.slf4j.LoggerFactory
import ru.cscenter.fingerpaint.api.*
import ru.cscenter.fingerpaint.db.Dao
import ru.cscenter.fingerpaint.db.entities.ChooseTask
import ru.cscenter.fingerpaint.db.entities.Image
import ru.cscenter.fingerpaint.db.entities.Statistic
import ru.cscenter.fingerpaint.db.entities.StatisticId
import spark.Request
import spark.Spark.*
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.sql.Date
import java.time.ZoneId
import java.time.ZonedDateTime


class Server {
    private val loginServer = AuthServer()
    private val logger = LoggerFactory.getLogger(Server::class.java)

    companion object {
        internal const val public = "/public"
        internal const val private = "/private"
        internal const val all = "/all"
        private const val okResponse = "ok"
        private const val patients = "$public/patients"
        private const val statistics = "$public/statistics"
        private const val login = "$public/login"
        private const val chooseTasks = "/choose"
        private const val images = "/images"
        private val timeZone = ZoneId.of("Europe/Moscow")

        private fun date() = ZonedDateTime.now(timeZone)
    }

    fun run() {
        val uploadDir = File("images")
        uploadDir.mkdir()

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

        post("$private$images/:name") { request, _ ->
            val fileName = request.params("name")
            val image = Dao.insertImage(Image(fileName))
            val file = File(uploadDir, fileName)

            request.raw().inputStream.use { input ->
                Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }

            image.id
        }

        get("$all$images/:id") { request, response ->
            val id = request.params("id").toLong()
            val image = Dao.selectImage(id)
            val file = File(uploadDir, image.path)
            response.type("image/jpeg")
            response.raw().contentType = "image/jpeg"
            response.header("Content-Disposition", "attachment; filename=${image.path}")
            val bytes = Files.readAllBytes(file.toPath())
            response.raw().outputStream.apply {
                write(bytes)
                flush()
                close()
            }
            response
        }

        get("$public$chooseTasks") { _, _ ->
            val tasks = Dao.selectChooseTasks()
                .map { ApiChooseTask(it.text, it.correctImageId, it.incorrectImageId) }
            toJsonArray(tasks)
        }

        post("$private$chooseTasks") { request, _ ->
            val task = getChooseTask(request)
            Dao.insertChooseTask(ChooseTask(task.text, task.correctImageId, task.incorrectImageId))
            okResponse
        }


        before("/*") { request, _ ->
            println(request.contentType())
            if (request.contentType() != "image/jpeg") {
                logger.info("${date()}\n\t>>>> ${request.requestMethod()} ${request.url()}\n\tbody: ${request.body()}")
            } else {
                logger.info("${date()}\n\t>>>> ${request.requestMethod()} ${request.url()}")
            }
        }

        after("/*") { request, response ->
            if (response.type() == null) {
                response.type("application/json")
            }
            logger.info("${date()}\n\t<<<< ${request.requestMethod()} ${request.url()} ${response.type()}\n\tbody: ${response.body()}")
        }
    }

    private fun getId(request: Request) = request.attribute<String>("id")
    private fun getPatientNames(request: Request) =
        fromJsonArray<ApiPatientName>(request.body())!!.map { it.name }

    private fun getChooseTask(request: Request) = fromJson<ApiChooseTask>(request.body())!!
    private fun getPatients(request: Request) = fromJsonArray<ApiPatient>(request.body())!!
    private fun getGameResults(request: Request) = fromJsonArray<ApiGameResult>(request.body())!!
    private fun getNewUsersStatistics(request: Request) =
        fromJsonArray<ApiNewPatientStatistics>(request.body())!!
}
