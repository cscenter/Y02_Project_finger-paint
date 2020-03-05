package ru.cscenter.fingerpaint.network

import retrofit2.Call
import retrofit2.http.*
import ru.cscenter.fingerpaint.api.*

private const val public = "/public"
private const val patients = "$public/patients"
private const val statistics = "$public/statistics"
private const val login = "$public/login"
private const val chooseTasks = "$public/choose"
private const val images = "/all/images/"

interface FingerPaintApi {
    @POST(login)
    fun login(): Call<String>

    @GET(patients)
    fun getPatients(): Call<List<ApiPatient>>

    @GET("$patients/exists/{id}")
    fun checkPatientExists(@Path("id") patientId: Long): Call<Boolean>

    @POST(patients)
    fun addPatients(@Body names: List<ApiPatientName>): Call<List<ApiPatient>>

    @PUT(patients)
    fun updatePatients(@Body patients: List<ApiPatient>): Call<String>

    @DELETE("$patients/{id}")
    fun deletePatient(@Path("id") patientId: Long): Call<String>

    @GET("$statistics/{id}")
    fun getStatistics(@Path("id") patientId: Long): Call<List<ApiStatistic>>

    @PUT(statistics)
    fun putStatistics(@Body results: List<ApiGameResult>): Call<String>

    @POST("$patients/new")
    fun addOfflinePatients(@Body patients: List<ApiNewPatientStatistics>): Call<String>

    @GET(chooseTasks)
    fun getChooseTasks(): Call<List<ApiChooseTask>>

    companion object {
        fun pictureUrl(pictureId: Long) = "${NetworkController.baseUrl}$images$pictureId"
    }
}
