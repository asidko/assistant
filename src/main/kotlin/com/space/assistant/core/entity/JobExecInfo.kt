package com.space.assistant.core.entity

interface JobExecInfo {
    val type: String
}

object JobExecType {
    const val REQUEST = "REQUEST"
    const val JUST_SAY = "JUST_SAY"
}

data class RequestJobExecInfo(val url: String) : JobExecInfo {
    override val type: String = JobExecType.REQUEST
}

data class JustSayJobExecInfo(val text: String) : JobExecInfo {
    override val type: String = JobExecType.JUST_SAY
}
