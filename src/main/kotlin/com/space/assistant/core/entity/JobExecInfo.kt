package com.space.assistant.core.entity

interface JobExecInfo {
    val type: String
}

object JobExecType {
    const val EMPTY = "EMPTY"
    const val PLUGIN = "PLUGIN"
    const val WIN_CMD = "WIN_CMD"
    const val REQUEST = "REQUEST"
    const val JUST_SAY = "JUST_SAY"
    const val WILDCARD = "WILDCARD"
    const val POWERSHELL = "POWERSHELL"
}

data class EmptyJobExecInfo(override val type: String = JobExecType.EMPTY) : JobExecInfo

data class PluginJobExecInfo(val name: String) : JobExecInfo {
    override val type: String = JobExecType.PLUGIN
}

data class RequestJobExecInfo(val url: String) : JobExecInfo {
    override val type: String = JobExecType.REQUEST
}

data class JustSayJobExecInfo(val text: String) : JobExecInfo {
    override val type: String = JobExecType.JUST_SAY
}

data class WinCmdJobExecInfo(val cmd: String) : JobExecInfo {
    override val type: String = JobExecType.WIN_CMD
}

data class PowerShellJobExecInfo(val cmd: String) : JobExecInfo {
    override val type: String = JobExecType.POWERSHELL
}


data class WildcardJobExecInfo(val text: String = "",
                               val expression: String = "") : JobExecInfo {
    override val type: String = JobExecType.WILDCARD
}
