package io.eventstime.utils

import java.text.Normalizer

fun String.removeAccents(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    val pattern = Regex("\\p{InCombiningDiacriticalMarks}+")
    return pattern.replace(normalized, "")
}
