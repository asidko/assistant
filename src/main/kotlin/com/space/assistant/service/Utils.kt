package com.space.assistant.service

import java.util.function.Function

fun <R, T : Function<R, R>> Iterable<T>.applyAsPipe(startValue: R): R {
    var result = startValue
    for (processor in this) result = processor.apply(result)
    return result
}

fun <R, T : Function<R, R?>> Iterable<T>.applyAsNullablePipe(startValue: R?): R? {
    startValue ?: return null
    var result = startValue
    for (processor in this) {
        result ?: return null
        result = processor.apply(result)
    }
    return result
}
