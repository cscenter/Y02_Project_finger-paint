package ru.cscenter.fingerpaint.network

import retrofit2.Call
import retrofit2.http.*
import ru.cscenter.fingerpaint.api.ApiGameResult
import ru.cscenter.fingerpaint.api.ApiPatient
import ru.cscenter.fingerpaint.api.ApiPatientName
import ru.cscenter.fingerpaint.api.ApiStatistic

private const val patients = "/patients"
private const val statistics = "/statistics"

interface FingerPaintApi {
    @GET(patients)
    fun getPatients(): Call<List<ApiPatient>>

    @GET("$patients/exists/{id}")
    fun checkPatientExists(@Path("id") patientId: Long): Call<Boolean>

    @POST(patients)
    fun addPatients(@Body names: List<ApiPatientName>): Call<List<ApiPatient>>

    @PUT(patients)
    fun updatePatient(@Body patient: ApiPatient): Call<String>

    @DELETE("$patients/{id}")
    fun deletePatient(@Path("id") patientId: Long): Call<String>

    @GET("$statistics/{id}")
    fun getStatistics(@Path("id") patientId: Long): Call<List<ApiStatistic>>

    @PUT(statistics)
    fun putStatistics(@Body result: ApiGameResult): Call<String>
}
