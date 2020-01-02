package com.space.assistant.service.filter

import com.space.assistant.core.entity.InputCommand
import com.space.assistant.core.service.InputCommandFilter
import com.space.assistant.service.applyAsNullablePipe
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Primary
@Service
class PrimaryCompositeCommandFilter(
        allFilters: List<InputCommandFilter>
) : InputCommandFilter {

    private val orders = mapOf(
            AssistantNameCommandFilter::class to 1
    )

    private val filters = allFilters
            .filterNot { it == this }
            .sortedWith(compareBy { orders[it::class] ?: Integer.MAX_VALUE })

    override fun apply(command: InputCommand): InputCommand? {
        return filters.applyAsNullablePipe(command)
    }
}
