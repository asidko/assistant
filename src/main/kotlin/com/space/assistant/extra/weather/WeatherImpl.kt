package com.space.assistant.extra.weather

import com.jayway.jsonpath.JsonPath
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt


class OpenWeatherAndWeatherbitWeatherDataProvider : WeatherDataProvider {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val client = OkHttpClient()

    private val apiKeyOpenweather = "d9c1d11ce435aa49358a7d58175b47c9"
    private val apiKeyWeatherbit = "ceb39bb90d634a65a8d7aa460450793d"

    private val cityId = "703448"

    private val dateToday: Instant get() = Instant.now()
    private val dateYesterday: Instant get() = dateToday.minus(1, ChronoUnit.DAYS)
    private val dateTomorrow: Instant get() = dateToday.plus(1, ChronoUnit.DAYS)

    private val openweatherCurrentRequest = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?id=$cityId&appid=$apiKeyOpenweather&units=metric")
            .get()
            .build()

    private fun weatherbitRequest(from: Instant, to: Instant) = Request.Builder()
            .url("https://api.weatherbit.io/v2.0/history/daily?city_id=$cityId&start_date=${from.format()}&end_date=${to.format()}&key=$apiKeyWeatherbit")
            .get()
            .build()

    override fun requestData(): WeatherData {
        val openweatherCurrent = sendRequest(openweatherCurrentRequest)
        val weatherbitYesterday = sendRequest(weatherbitRequest(dateYesterday, dateToday))
        val weatherbitToday = sendRequest(weatherbitRequest(dateToday, dateTomorrow))

        val tempCurrent: Double = openweatherCurrent.read("$.main.temp")
        val tempToday: Double = weatherbitToday.read("$.data[0].temp")
        val tempYesterday: Double = weatherbitYesterday.read("$.data[0].temp")
        val skyState: String = openweatherCurrent.read("$.weather[0].main")
        val windSpeed: Double = openweatherCurrent.read("$.wind.speed")

        log.debug("Weather temp current={}, today={}, yesterday={}; wind={}; sky={}",
                tempCurrent, tempToday, tempYesterday,
                windSpeed, skyState)

        return object : WeatherData {
            override val currentTemp = tempCurrent.roundToInt()
            override val yesterdayTempDiff = (tempToday - tempYesterday).roundToInt()
            override val skyState = when (skyState) {
                "Clear" -> WeatherSkyState.SUN
                "Clouds" -> WeatherSkyState.CLOUD
                "Rain" -> WeatherSkyState.RAIN
                "Drizzle" -> WeatherSkyState.SMALL_RAIN
                else -> WeatherSkyState.CLOUD
            }
            override val windState = when {
                windSpeed <= 3 -> WeatherWindState.ABSENT
                windSpeed <= 6 -> WeatherWindState.SOFT
                else -> WeatherWindState.STRONG
            }
        }
    }

    private fun sendRequest(request: Request) =
            client.newCall(request).execute()
                    .let { response -> response.body()?.string() ?: "{}" }
                    .let { json -> JsonPath.parse(json) }

    private fun Instant.format(): String = SimpleDateFormat("yyyy-MM-dd").format(Date.from(this))
}


class DefaultWeatherMessageProvider : WeatherMessageProvider {
    override fun getMessage(data: WeatherData): String {
        val intro = weatherMessages["INTRO"]?.random()

        val temperatureNow = weatherMessages["TEMPERATURE_NOW"]?.random()?.replace("%s", data.currentTemp.toString())

        val temperatureToYesterday = when {
            data.yesterdayTempDiff > 1 -> weatherMessages["TEMPERATURE_TO_YESTERDAY_WARM_FOR"]
            data.yesterdayTempDiff < -1 -> weatherMessages["TEMPERATURE_TO_YESTERDAY_COLDER"]
            else -> weatherMessages["TEMPERATURE_TO_YESTERDAY_EQUAL"]
        }?.random()?.replace("%s", abs(data.yesterdayTempDiff).toString())

        val skyState = when (data.skyState) {
            WeatherSkyState.RAIN -> weatherMessages["SKY_RAIN"]
            WeatherSkyState.SMALL_RAIN -> weatherMessages["SKY_SMALL_RAIN"]
            WeatherSkyState.CLOUD -> weatherMessages["SKY_CLOUD"]
            WeatherSkyState.SUN -> weatherMessages["SKY_SUN"]
        }?.random()

        val windState = when (data.windState) {
            WeatherWindState.ABSENT -> weatherMessages["WIND_ABSENT"]
            WeatherWindState.SOFT -> weatherMessages["WIND_SOFT"]
            WeatherWindState.STRONG -> weatherMessages["WIND_STRONG"]
        }?.random()

        return "$intro. " +
                "$temperatureNow. $temperatureToYesterday. " +
                "$skyState. " +
                "$windState."
    }
}

val weatherMessages = mapOf(
        "INTRO" to listOf("По погоде на сегодня", "По поводу погоды", "Касательно погоды", "Информация о погоде"),
        "TEMPERATURE_NOW" to listOf("Сейчас за окном %s °", "Сейчас %s °C", "На данный момент %s °"),
        "TEMPERATURE_TO_YESTERDAY_EQUAL" to listOf("Температура особо не поменялась", "Температура такая же как и вчера", "По сравнению со вчера ничего не изменилось"),
        "TEMPERATURE_TO_YESTERDAY_WARM_FOR" to listOf("На %s ° теплее чем вчера", "Это на %s ° теплее чем было вчера", "По сравнению со вчера - на %s ° теплее"),
        "TEMPERATURE_TO_YESTERDAY_COLD_FOR" to listOf("На %s ° холоднее чем вчера", "Это на %s ° холоднее чем было вчера", "По сравнению со вчера - на %s ° теплее"),
        "SKY_SMALL_RAIN" to listOf("Идет небольшой дождь", "Небольшой дождь"),
        "SKY_RAIN" to listOf("Идет дождь", "Дождь"),
        "SKY_CLOUD" to listOf("Небо облачное"),
        "SKY_SUN" to listOf("Ясная хорошая погода", "Солнечный хороший день"),
        "WIND_ABSENT" to listOf("Ветра нету", "Ветер отсутствует", "Практически без ветра",  "Ветра почти нету"),
        "WIND_SOFT" to listOf("Слабый ветер", "Не сильный ветер"),
        "WIND_STRONG" to listOf("Ветряно", "Довольно сильный ветер")
)
