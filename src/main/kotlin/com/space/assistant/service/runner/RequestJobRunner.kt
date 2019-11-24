package com.space.assistant.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.RequestJobExecInfo
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.net.URL

@Service
class RequestJobRunner(
        private val objectMapper: ObjectMapper
) : JobRunner {

    override fun runJob(jobInfo: JobInfo, previousJobResult: JobResult?): Mono<JobResult> {
        if (!canRun(jobInfo)) return Mono.empty()

        return Mono.create {
            val url = previousJobResult?.result ?: (jobInfo.execInfo as RequestJobExecInfo).url
            val json = sendRequest(url)
            val result = previousJobResult?.copy(result = json)
                    ?: JobResult.new(json, jobInfo)

            it.success(result)
        }
    }

    private fun canRun(jobInfo: JobInfo) =
            jobInfo.execInfo.type == JobExecType.REQUEST

    private fun sendRequest(url: String): String {
        if (true) return mockResponse
        return URL(url)
                .openConnection()
                .getInputStream().use {
                    objectMapper.readTree(it).toString()
                }
    }

    val mockResponse = """
        {"consolidated_weather":[{"id":6393993367650304,"weather_state_name":"Light Cloud","weather_state_abbr":"lc","wind_direction_compass":"ESE","created":"2019-11-22T15:32:01.917991Z","applicable_date":"2019-11-22","min_temp":-7.03,"max_temp":-3.575,"the_temp":-3.3449999999999998,"wind_speed":7.893395472952624,"wind_direction":113.49996191774734,"air_pressure":1035.5,"humidity":41,"visibility":16.10998021554124,"predictability":70},{"id":6070177361821696,"weather_state_name":"Heavy Cloud","weather_state_abbr":"hc","wind_direction_compass":"SE","created":"2019-11-22T15:32:04.525174Z","applicable_date":"2019-11-23","min_temp":-6.495,"max_temp":-1.6600000000000001,"the_temp":-3.075,"wind_speed":7.127561663864366,"wind_direction":127.33237777578917,"air_pressure":1032.0,"humidity":44,"visibility":17.458045017100133,"predictability":71},{"id":5095394771992576,"weather_state_name":"Light Cloud","weather_state_abbr":"lc","wind_direction_compass":"SE","created":"2019-11-22T15:32:07.658268Z","applicable_date":"2019-11-24","min_temp":-5.725,"max_temp":-0.45500000000000007,"the_temp":-1.99,"wind_speed":6.045502421442774,"wind_direction":128.3299021184476,"air_pressure":1028.5,"humidity":46,"visibility":18.06574604310825,"predictability":70},{"id":5118260104134656,"weather_state_name":"Light Cloud","weather_state_abbr":"lc","wind_direction_compass":"SE","created":"2019-11-22T15:32:10.739547Z","applicable_date":"2019-11-25","min_temp":-3.785,"max_temp":0.48500000000000004,"the_temp":-1.455,"wind_speed":5.211747397998356,"wind_direction":128.32956305530232,"air_pressure":1024.5,"humidity":65,"visibility":16.187962300167023,"predictability":70},{"id":4533663515541504,"weather_state_name":"Heavy Cloud","weather_state_abbr":"hc","wind_direction_compass":"SE","created":"2019-11-22T15:32:13.657151Z","applicable_date":"2019-11-26","min_temp":-1.6949999999999998,"max_temp":1.3199999999999998,"the_temp":-0.01999999999999999,"wind_speed":4.284341289310427,"wind_direction":131.9996952201138,"air_pressure":1019.0,"humidity":79,"visibility":15.252177284657598,"predictability":71},{"id":4575816069414912,"weather_state_name":"Heavy Cloud","weather_state_abbr":"hc","wind_direction_compass":"SSE","created":"2019-11-22T15:32:16.634651Z","applicable_date":"2019-11-27","min_temp":-0.255,"max_temp":2.205,"the_temp":0.61,"wind_speed":4.545064821442774,"wind_direction":160.50000000000003,"air_pressure":1016.0,"humidity":79,"visibility":9.999726596675416,"predictability":71}],"time":"2019-11-22T19:23:41.623301+02:00","sun_rise":"2019-11-22T07:22:40.460348+02:00","sun_set":"2019-11-22T16:04:42.619388+02:00","timezone_name":"LMT","parent":{"title":"Ukraine","location_type":"Country","woeid":23424976,"latt_long":"48.382881,31.173441"},"sources":[{"title":"BBC","slug":"bbc","url":"http://www.bbc.co.uk/weather/","crawl_rate":360},{"title":"Forecast.io","slug":"forecast-io","url":"http://forecast.io/","crawl_rate":480},{"title":"Met Office","slug":"met-office","url":"http://www.metoffice.gov.uk/","crawl_rate":180},{"title":"OpenWeatherMap","slug":"openweathermap","url":"http://openweathermap.org/","crawl_rate":360},{"title":"World Weather Online","slug":"world-weather-online","url":"http://www.worldweatheronline.com/","crawl_rate":360}],"title":"Kiev","location_type":"City","woeid":924938,"latt_long":"50.441380,30.522490","timezone":"Europe/Kiev"}
    """.trimIndent()
}
