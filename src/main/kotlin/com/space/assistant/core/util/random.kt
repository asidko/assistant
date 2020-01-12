package com.space.assistant.core.util

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')


fun simpleID(length: Int = 4): String = (1..length)
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")