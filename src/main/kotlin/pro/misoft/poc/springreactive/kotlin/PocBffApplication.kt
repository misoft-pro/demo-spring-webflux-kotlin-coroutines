package pro.misoft.poc.springreactive.kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class PocBffServerApplication

fun main(args: Array<String>) {
    runApplication<PocBffServerApplication>(*args)
}
