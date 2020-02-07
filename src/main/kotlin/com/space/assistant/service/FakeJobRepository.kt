package com.space.assistant.service

import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobPhrase
import com.space.assistant.core.entity.Phrase
import com.space.assistant.core.service.JobRepository
import com.space.assistant.service.parser.EmptyJobResultParser
import com.space.assistant.service.parser.JsonPathJobResultParser
import com.space.assistant.service.runner.*
import com.space.assistant.service.search.CronJobActivator
import com.space.assistant.service.search.DirectMatchJobActivator
import com.space.assistant.service.search.EmptyJobFinderActivator
import com.space.assistant.service.search.WildcardJobActivator
import org.springframework.stereotype.Service

@Service
class FakeJobRepository : JobRepository {

    override fun findJobByPhrase(phrase: Phrase): JobInfo? {
        val phraseText = phrase.joinToString(" ")
        return jobs
                .filter { it.activatorInfo is DirectMatchJobActivator.Info }
                .find { (it.activatorInfo as DirectMatchJobActivator.Info).texts.contains(phraseText) }
    }

    override fun findJobByUuid(uuid: String): JobInfo? {
        return jobs.find { it.uuid == uuid }
    }

    override fun findJobsByActivatorType(activatorType: String): List<JobInfo> {
        return jobs.filter { it.activatorInfo.type == activatorType }
    }

    private val jobs = listOf(
            JobInfo(
                    uuid = "TEST",
                    activatorInfo = DirectMatchJobActivator.Info(texts = listOf("прием", "прийом", "чуєш", "ти чуєш", "ты тут", "ты здесь", "слышишь", "ты слышишь")),
                    phrase = JobPhrase(before = listOf("Слышу вас", "Я здесь", "Я наместе", "Все нормально", "Да-да", "Работаю"), after = listOf()),
                    runnerInfo = EmptyJobRunner.Info(),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf()
            ),
            JobInfo(
                    uuid = "SAY_TEXT",
                    activatorInfo = EmptyJobFinderActivator.Info(),
                    phrase = JobPhrase(before= listOf(), after = listOf()),
                    runnerInfo = JustSayJobRunner.Info(text = ""),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf()
            ),
            JobInfo(
                    uuid = "SAY_WEATHER",
                    activatorInfo = DirectMatchJobActivator.Info(texts = listOf("погода")),
                    phrase = JobPhrase(before= listOf("уточняю температуру", "секундочку", "смотрю"), after = listOf()),
                    runnerInfo = RequestJobRunner.Info(url = "https://www.metaweather.com/api/location/924938/"),
                    resultParserInfo = JsonPathJobResultParser.Info(
                            jsonPathValues = listOf("\$.consolidated_weather[0].the_temp"),
                            resultFormatString = "Текущая температура $1 градусов"),
                    redirectToJobs = listOf("SAY_TEXT")
            ),
            JobInfo(
                    uuid = "RADIO",
                    activatorInfo = DirectMatchJobActivator.Info(texts = listOf("включи радио")),
                    phrase = JobPhrase(before= listOf("включаю", "открываю", "запускаю", "сейчас будет"), after = listOf()),
                    runnerInfo = WinCmdJobRunner.Info(cmd = "http://www.hitfm.ua/player/"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf("SAY_TEXT")
            ),
            JobInfo(
                    uuid = "RADIO_OFF",
                    activatorInfo = DirectMatchJobActivator.Info(texts = listOf("выключи радио", "выключить радио")),
                    phrase = JobPhrase(before= listOf("выключаю"), after = listOf()),
                    runnerInfo = PowershellJobRunner.Info(cmd = "taskkill /F /IM chrome.exe /T"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf()
            ),
            JobInfo(
                    uuid = "GOOGLE_SEARCH",
                    activatorInfo = WildcardJobActivator.Info(text = "найти *"),
                    phrase = JobPhrase(before= listOf("выполняю поиск", "окей, ищу"), after = listOf()),
                    runnerInfo = WildcardJobRunner.Info(wildcardText = "", resultText = "https://www.google.com/search?q=$1"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf()
            ),
            JobInfo(
                    uuid = "VOLUME_UP",
                    activatorInfo = DirectMatchJobActivator.Info(texts = listOf("volume up")),
                    phrase = JobPhrase(before= listOf(), after = listOf("Звук повышен", "Звук увеличен")),
                    runnerInfo = PowershellJobRunner.Info(cmd = "\$obj = new-object -com wscript.shell; \$obj.SendKeys([char]174)"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf()
            ),
            JobInfo(
                    uuid = "10_SECONDS",
                    activatorInfo = DirectMatchJobActivator.Info(texts = listOf("10 секунд")),
                    phrase = JobPhrase(before= listOf("Засекаю 10 секунд", "Отсчитываю 10 секунд", "Таймер на 10 секунд установлен"), after = listOf("10 секунд прошло", "Время вышло")),
                    runnerInfo = TimeDelayJobRunner.Info(seconds = "10"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf()
            ),
            JobInfo(
                    uuid = "MORNING_WEATHER",
                    activatorInfo = CronJobActivator.Info(cron = "00 56 12 * * *"),
                    phrase = JobPhrase(before= listOf(), after = listOf()),
                    runnerInfo = JustSayJobRunner.Info(text = "Добрый вечер"),
                    resultParserInfo = EmptyJobResultParser.Info(),
                    redirectToJobs = listOf()
            )
    )

}
