package com.space.assistant.extra.weather

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.roundToInt


/*
 * ---------- Example of text: ----------
 * По погоде на сегодня.
 * Сейчас за окном 10 градусов, немного теплее чем вчера.
 * Идет дождь. / Облачно, но дождя не предвидится. / Ясная хорошая погода
 * Сильный ветер
 * Вероятность дождя высокая
 */

enum class WeatherSkyState {
    RAIN, CLOUD, SUN, SMALL_RAIN
}

enum class WeatherWindState {
    ABSENT, SOFT, STRONG
}

interface WeatherData {
    val currentTemp: Int
    val yesterdayTempDiff: Int
    val skyState: WeatherSkyState
    val windState: WeatherWindState
}

interface WeatherDataProvider {
    fun requestData(): WeatherData
}

interface WeatherMessageProvider {
    fun getMessage(data: WeatherData): String
}
