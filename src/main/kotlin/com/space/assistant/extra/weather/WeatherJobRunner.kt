package com.space.assistant.extra.weather

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.JobRunnerInfo
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service

@Service
class WeatherJobRunner : JobRunner {
    companion object {
        const val typeName = "WEATHER"
    }

    data class Info(override val type: String = typeName) : JobRunnerInfo

    val weatherDataProvider: WeatherDataProvider = OpenWeatherAndWeatherbitWeatherDataProvider()
    val weatherMessageProvider: WeatherMessageProvider = DefaultWeatherMessageProvider()

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        if (activeJobInfo.jobInfo?.runnerInfo !is Info) return null

        val data = weatherDataProvider.requestData()
        val text = weatherMessageProvider.getMessage(data)

        return JobResult(text)
    }

}
