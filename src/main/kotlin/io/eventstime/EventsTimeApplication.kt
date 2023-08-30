package io.eventstime

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventsTimeApplication

fun main(args: Array<String>) {
    runApplication<EventsTimeApplication>(*args)
}
