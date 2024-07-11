package pro.misoft.poc.springreactive.kotlin.infra.spring.tracing

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationPredicate
import io.micrometer.tracing.Tracer
import io.micrometer.tracing.propagation.Propagator
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator
import io.opentelemetry.extension.trace.propagation.B3Propagator
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.env.Environment
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


private const val X_TRACE_ID = "X-B3-TraceId"
private const val X_SPAN_ID = "X-B3-SpanId"

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
class TracingConfig {

//    @Bean
//    fun webClientCustomizer(tracer: Tracer, propagator: Propagator): WebClientCustomizer {
//        return WebClientCustomizer { it.filter(addTracingHeadersToDownstream(tracer, propagator)) }
//    }
//
//    private fun addTracingHeadersToDownstream(tracer: Tracer, propagator: Propagator): ExchangeFilterFunction {
//        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
//            val span = tracer.currentSpan()!!
//            val newRequest = ClientRequest.from(clientRequest).build()
//            propagator.inject(span.context(), newRequest) { carrier, key, value ->
//                carrier!!.headers().add(key, value)
//            }
//            Mono.just(newRequest)
//        }
//    }

//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    fun tracingFilter(tracer: Tracer, propagator: Propagator): WebFilter {
//
//        return TracingWebFilter(tracer, propagator)
//    }

    @Bean
    fun contextW3Propagator(): W3CBaggagePropagator {
        return W3CBaggagePropagator.getInstance()
    }

    @Bean
    fun contextB3Propagator(): B3Propagator {
        return B3Propagator.injectingMultiHeaders()
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

    class TracingWebFilter(private val tracer: Tracer, private val propagator: Propagator) : WebFilter {

        override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
            val headers = exchange.request.headers
            val traceContext = propagator.extract(headers) { carrier, key -> headers.getFirst(key) }

            val span = tracer.nextSpan(traceContext.start()).name("http_request").start()
            exchange.response.headers.run {
                add(X_TRACE_ID, span.context().traceId())
                add(X_SPAN_ID, span.context().spanId())
            }
            return chain.filter(exchange)
                .doOnTerminate { span.end() }
        }
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


//    class TracingWebFilter(private val tracer: Tracer, private val propagator: Propagator) : WebFilter {
//
//        override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
//            val headers = exchange.request.headers
//            val traceId = headers.getFirst("X-Trace-Id")
//            val parentSpanId = headers.getFirst("X-Parent-Span-ID")
//            val newContext: Any = if (traceId != null && parentSpanId != null) {
//                val traceContext = propagator.extract(traceId) { carrier, key -> headers.getFirst(key) }
//                traceContext
//            } else {
//                tracer.currentSpan()!!.context()
//            }
//
//            val span = tracer.nextSpan(newContext).name("incoming_http_request").start()
//            exchange.response.headers.add(X_TRACE_ID, span.context().traceId())
//            return chain.filter(exchange)
//                .doOnTerminate { span.end() }
//        }
//    }

//
//    private fun addTraceIdFilterFunction(tracer: Tracer): ExchangeFilterFunction {
//        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
//            val traceId = tracer.currentSpan()?.context()?.traceId() ?: "no-trace"
//            val newRequest = ClientRequest.from(clientRequest)
//                .header(X_TRACE_ID, traceId)
//                .build()
//            Mono.just(newRequest)
//        }
//    }

}