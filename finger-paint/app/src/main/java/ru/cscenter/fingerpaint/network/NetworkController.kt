package ru.cscenter.fingerpaint.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.api.ApiGameResult
import ru.cscenter.fingerpaint.api.ApiPatient
import ru.cscenter.fingerpaint.api.ApiStatistic
import ru.cscenter.fingerpaint.db.*
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

typealias SuccessHandler<T> = (result: T) -> Unit
typealias FailHandler = (code: Int) -> Unit

const val NETWORK_ERROR_CODE = 600

fun codeIsError(code: Int) = code == NETWORK_ERROR_CODE || code == 401

fun <T> Call<T>.executeAsync(
    onSuccess: SuccessHandler<T>,
    onFail: FailHandler
) = enqueue(object : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        t.printStackTrace()
        onFail(NETWORK_ERROR_CODE)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            Log.e("FingerPaint", "Response error ${response.message()}")
            onFail(response.code())
            return
        }
        onSuccess(body)
    }
})

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
private fun longDateToString(date: Long) = dateFormat.format(Date(date))
private fun dateStringToLong(date: String): Long {
    val calendar = GregorianCalendar(TimeZone.getTimeZone("UTC"))
    calendar.time = dateFormat.parse(date)!!
    return dateToLong(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        1 + calendar.get(Calendar.DAY_OF_MONTH)
    )
}

fun ApiPatient.toUser() = User(id.toInt(), name)
fun ApiStatistic.toStatistic(id: Int) = Statistic(
    userId = id,
    date = dateStringToLong(date),
    type = TypeConverter().toGameType(type)!!,
    total = total,
    success = success
)

fun cachedResultToApiResult(result: CachedGameResult) = ApiGameResult(
    patientId = result.userId.toLong(),
    date = longDateToString(result.date),
    type = result.type.id,
    success = result.success
)

fun statisticToApiResult(statistic: Statistic, gameResult: GameResult) = ApiGameResult(
    patientId = statistic.userId.toLong(),
    date = longDateToString(statistic.date),
    type = statistic.type.id,
    success = gameResult == GameResult.SUCCESS
)

fun statisticToApi(statistic: Statistic) = ApiStatistic(
    date = longDateToString(statistic.date),
    type = statistic.type.id,
    total = statistic.total,
    success = statistic.success
)

class NetworkController {
    val api: FingerPaintApi
    var idToken: String = ""

    init {

        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request =
                    chain.request().newBuilder().addHeader("access_key", idToken).build()
                chain.proceed(request)
            }
            .addInterceptor(logger)
            .readTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(MainApplication.baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        api = retrofit.create(FingerPaintApi::class.java)
    }
}
