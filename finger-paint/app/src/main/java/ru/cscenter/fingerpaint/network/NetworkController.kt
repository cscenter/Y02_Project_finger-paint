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
import ru.cscenter.fingerpaint.api.ApiGameResult
import ru.cscenter.fingerpaint.api.ApiPatient
import ru.cscenter.fingerpaint.api.ApiStatistic
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.TypeConverter
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.db.dateToLong
import ru.cscenter.fingerpaint.ui.games.base.GameResult
import java.text.SimpleDateFormat
import java.util.*

typealias SuccessHandler<T> = (result: T) -> Unit
typealias FailHandler<T> = (call: Call<T>, onSuccess: SuccessHandler<T>, code: Int) -> Unit

fun <T> Call<T>.executeAsync(
    onSuccess: SuccessHandler<T>,
    onFail: FailHandler<T>
) = enqueue(object : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        t.printStackTrace()
        onFail(call, onSuccess, 600)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            Log.e("FingerPaint", "Response error ${response.message()}")
            onFail(call, onSuccess, response.code())
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
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

fun ApiPatient.toUser() = User(id.toInt(), name)
fun ApiStatistic.toStatistic() = Statistic(
    userId = patientId.toInt(),
    date = dateStringToLong(date),
    type = TypeConverter().toGameType(type)!!,
    total = total,
    success = success
)

fun statisticToApi(statistic: Statistic, gameResult: GameResult) = ApiGameResult(
    patientId = statistic.userId.toLong(),
    date = longDateToString(statistic.date),
    type = statistic.type.id,
    success = gameResult == GameResult.SUCCESS
)

class NetworkController {
    val api: FingerPaintApi
    var idToken: String? = null

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
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        api = retrofit.create(FingerPaintApi::class.java)
    }

    companion object {
        private const val baseUrl =
            "http://ec2-54-213-235-214.us-west-2.compute.amazonaws.com:4567/"
    }
}
