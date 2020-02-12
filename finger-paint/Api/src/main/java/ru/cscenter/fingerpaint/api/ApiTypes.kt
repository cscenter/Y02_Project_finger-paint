package ru.cscenter.fingerpaint.api

import com.beust.klaxon.Klaxon

interface ApiBase

inline fun <reified T : ApiBase> fromJson(string: String): T? = Klaxon().parse<T>(string)

inline fun <reified T : ApiBase> toJson(api: T): String = Klaxon().toJsonString(api)

inline fun <reified T : ApiBase> fromJsonArray(list: String): List<T>? = Klaxon().parseArray(list)

inline fun <reified T : ApiBase> toJsonArray(list: List<T>): String = Klaxon().toJsonString(list)

data class ApiPatient(val id: Long, val name: String) : ApiBase
data class ApiPatientName(val name: String) : ApiBase
data class ApiStatistic(
    val patientId: Long,
    val date: String,
    val type: Int,
    val total: Int,
    val success: Int
) : ApiBase

data class ApiGameResult(
    val patientId: Long,
    val date: String,
    val type: Int,
    val success: Boolean
) : ApiBase
