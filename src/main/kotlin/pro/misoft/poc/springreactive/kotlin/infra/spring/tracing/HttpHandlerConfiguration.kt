package pro.misoft.poc.springreactive.kotlin.infra.spring.tracing

import io.micrometer.observation.ObservationRegistry
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.ContextPathCompositeHandler
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.util.StringUtils
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import java.util.*


@Configuration(proxyBeanMethods = false)
class HttpHandlerConfiguration(private val applicationContext: ApplicationContext) {

    @Bean
    fun httpHandler(propsProvider: ObjectProvider<WebFluxProperties>, registry: ObservationRegistry): HttpHandler {
        val httpHandler = WebHttpHandlerBuilder
            .applicationContext(this.applicationContext)
            .observationRegistry(registry)
            .build()
        val properties = propsProvider.getIfAvailable()
        if (properties != null && StringUtils.hasText(properties.basePath)) {
            val handlersMap = Collections.singletonMap(properties.basePath, httpHandler)
            return ContextPathCompositeHandler(handlersMap)
        }
        return httpHandler
    }
}