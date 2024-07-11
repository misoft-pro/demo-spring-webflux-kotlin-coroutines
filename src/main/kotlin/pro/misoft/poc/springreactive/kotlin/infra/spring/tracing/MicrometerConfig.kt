package pro.misoft.poc.springreactive.kotlin.infra.spring.tracing

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationPredicate
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import io.micrometer.tracing.Tracer
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.env.Environment
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain


private const val X_TRACE_ID = "X-B3-TraceId"
private const val X_SPAN_ID = "X-B3-SpanId"

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
class MicrometerConfig {

    @Bean
    fun observedAspect(observationRegistry: ObservationRegistry): ObservedAspect {
        return ObservedAspect(observationRegistry)
    }

    @Bean
    fun noActuatorServerObservations(): ObservationPredicate {
        return ObservationPredicate { name, context ->
            if (name == "http.server.requests" && context is ServerRequestObservationContext) {
                !context.carrier.uri.path.contains("/actuator")
            } else {
                true
            }
        }
    }

    @Bean
    fun metricsCommonTags(env: Environment): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer<MeterRegistry> { registry: MeterRegistry ->
            val profiles = java.lang.String.join(",", *env.activeProfiles)
            registry.config().commonTags("profile", if (profiles.isNullOrEmpty()) "localhost" else profiles)
        }
    }

    /**
     * This and @EnableAspectJAutoProxy are required so that we can use the @Timed annotation
     * on methods that we want to time.
     * See: [Micrometer AOP config](https://micrometer.io/docs/concepts#_the_timed_annotation)
     */
    @Bean
    fun timedAspect(registry: MeterRegistry): TimedAspect {
        return TimedAspect(registry)
    }

    @Bean
    fun traceIdInResponseFilter(tracer: Tracer): WebFilter {
        return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
            val currentSpan: io.micrometer.tracing.Span? = tracer.currentSpan()
            if (currentSpan != null) {
                // putting trace id value in [traceId] response header
                exchange.response.headers.add(X_TRACE_ID, currentSpan.context().traceId())
                exchange.response.headers.add(X_SPAN_ID, currentSpan.context().spanId())
            }
            chain.filter(exchange)
        }
    }
}