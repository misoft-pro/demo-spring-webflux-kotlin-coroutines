package pro.misoft.poc.springreactive.kotlin.infra.spring.filter

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
@Order(1)  // Ensure this filter runs before other filters
class ApiCallCounterFilter(meterRegistry: MeterRegistry) : WebFilter {

    private val apiCallCounter: Counter = meterRegistry.counter("custom.api.calls.total")
    private val authApiCallCounter: Counter = meterRegistry.counter("custom.api.calls.auth")

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (!exchange.request.uri.path.contains("/actuator")) {
            apiCallCounter.increment()
        }
        if (exchange.request.uri.path.contains("/auth")) {
            authApiCallCounter.increment()
        }
        return chain.filter(exchange)
    }
}
