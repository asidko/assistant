package com.space.assistant.addons.weather

import kotlin.math.abs

/*
 * ---------- Example of text: ----------
 * По погоде на сегодня.
 * Сейчас за окном 10 градусов, немного теплее чем вчера.
 * Идет дождь. / Облачно, но дождя не предвидится. / Ясная хорошая погода
 * Сильный ветер
 * Вероятность дождя высокая
 */

enum class WeatherSkyState {
    RAIN,
    CLOUD,
    PARTLY_CLOUD,
    SUN
}

enum class WeatherWindState {
    ABSENT,
    SOFT,
    STRONG
}

interface WeatherData {
    val currentTemp: Int
    val yesterdayTemp: Int
    val skyState: WeatherSkyState
    val windState: WeatherWindState
}

interface WeatherMessageProvider {
    fun getMessage(data: WeatherData): String
}

class DefaultWeatherMessageProvider : WeatherMessageProvider {
    override fun getMessage(data: WeatherData): String {
        val intro = weatherMessages["INTRO"]?.random()
        val temperatureNow = weatherMessages["TEMPERATURE_NOW"]?.random()?.replace("%s", data.currentTemp.toString())
        val temperatureToYesterday = when {
            data.currentTemp > data.yesterdayTemp + 1 -> weatherMessages["TEMPERATURE_TO_YESTERDAY_WARM_FOR"]
            data.currentTemp < data.yesterdayTemp - 1 -> weatherMessages["TEMPERATURE_TO_YESTERDAY_COLDER"]
            else -> weatherMessages["TEMPERATURE_TO_YESTERDAY_EQUAL"]
        }?.random()?.replace("%s", abs(data.currentTemp - data.yesterdayTemp).toString())
        val skyState = when (data.skyState) {
            WeatherSkyState.RAIN -> weatherMessages["SKY_RAIN"]
            WeatherSkyState.CLOUD -> weatherMessages["SKY_CLOUD"]
            WeatherSkyState.PARTLY_CLOUD -> weatherMessages["SKY_PARTLY_CLOUD"]
            WeatherSkyState.SUN -> weatherMessages["SKY_SUN"]
        }?.random()
        val windState = when (data.windState) {
            WeatherWindState.ABSENT -> weatherMessages["WIND_ABSENT"]
            WeatherWindState.SOFT -> weatherMessages["SOFT"]
            WeatherWindState.STRONG -> weatherMessages["WIND_STRONG"]
        }

        return "$intro. " +
                "$temperatureNow. $temperatureToYesterday." +
                "$skyState." +
                "$windState."
    }
}

val weatherMessages = mapOf(
        "INTRO" to listOf("По погоде на сегодня"),
        "TEMPERATURE_NOW" to listOf("Сейчас за окном %s"),
        "TEMPERATURE_TO_YESTERDAY_EQUAL" to listOf("Температура особо не поменялась"),
        "TEMPERATURE_TO_YESTERDAY_WARM_FOR" to listOf("На %s теплее чем вчера"),
        "TEMPERATURE_TO_YESTERDAY_COLD_FOR" to listOf("На %s холоднее чем вчера"),
        "SKY_RAIN" to listOf("Идет дождь"),
        "SKY_CLOUD" to listOf("Небо облачное"),
        "SKY_PARTLY_CLOUD" to listOf("В основном ясная погода"),
        "SKY_SUN" to listOf("Ясная хорошая погода"),
        "WIND_ABSENT" to listOf("Ветра нету", "Ветер отсутствует"),
        "WIND_SOFT" to listOf("Слабый ветер", "Ветра почти нету", "Ветер почти отсутствует"),
        "WIND_STRONG" to listOf("Ветряно", "Довольно сильный ветер")
)
