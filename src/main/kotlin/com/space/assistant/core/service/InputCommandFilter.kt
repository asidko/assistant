package com.space.assistant.core.service

import com.space.assistant.core.entity.InputCommand
import java.util.function.Function

interface InputCommandFilter : Function<InputCommand, InputCommand?>
