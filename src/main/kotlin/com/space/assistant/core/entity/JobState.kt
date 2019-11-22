package com.space.assistant.core.entity

import java.time.Instant
import java.util.*

class JobState (
    val uuid: UUID = UUID.randomUUID(),
    val time: Instant = Instant.now(),
    val commandUuid: UUID,
    val jobInfoUuid: UUID,
    val isActive: Boolean
)
