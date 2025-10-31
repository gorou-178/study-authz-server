package org.example.study

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StudyAuthzServerApplication

fun main(args: Array<String>) {
    runApplication<StudyAuthzServerApplication>(*args)
}
