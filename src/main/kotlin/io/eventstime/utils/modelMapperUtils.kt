package io.eventstime.utils

import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component

@Component
class modelMapperUtils {
    fun <T> copyAttributes(source: T, target: T): T {
        val modelMapper = ModelMapper()

        modelMapper.configuration.apply {
            isDeepCopyEnabled = true
            isFieldMatchingEnabled = true
        }

        modelMapper.map(source, target)
        return target
    }
}
