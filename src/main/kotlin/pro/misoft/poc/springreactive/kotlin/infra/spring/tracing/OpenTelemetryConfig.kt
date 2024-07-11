package pro.misoft.poc.springreactive.kotlin.infra.spring.tracing

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.extension.trace.propagation.B3Propagator
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.logs.LogRecordProcessor
import io.opentelemetry.sdk.logs.SdkLoggerProvider
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import io.opentelemetry.semconv.ResourceAttributes
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class OpenTelemetryConfig {
    @Value("\${opentelemetry.loggingendpoint:http://localhost:4317}")
    private val loggingEndpoint: String? = null

    @Value("\${management.otlp.tracing.endpoint:http://localhost:4317/v1/traces}")
    private val tracingEndpoint: String? = null

    @Bean
    fun contextB3Propagator(): B3Propagator {
        return B3Propagator.injectingMultiHeaders()
    }

    @Bean
    fun openTelemetry(sdkLoggerProvider: SdkLoggerProvider?): OpenTelemetry {
        val resource = Resource.getDefault()
            .merge(
                Resource.create(
                    Attributes.of(
                        ResourceAttributes.SERVICE_NAME, "Main Backend"
                    )
                )
            )

        val sdkTracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(
                BatchSpanProcessor.builder(
                    OtlpGrpcSpanExporter.builder()
                        .setEndpoint(tracingEndpoint!!)
                        .build()
                ).build()
            )
            .setResource(resource)
            .build()

        val openTelemetrySdk = OpenTelemetrySdk.builder()
            .setLoggerProvider(sdkLoggerProvider!!)
            .setTracerProvider(sdkTracerProvider)
            .setPropagators(ContextPropagators.create(B3Propagator.injectingMultiHeaders()))
            .build()
//        OpenTelemetryAppender.install(openTelemetrySdk)
        return openTelemetrySdk
    }

    @Bean
    fun otelSdkLoggerProvider(
        environment: Environment,
        logRecordProcessors: ObjectProvider<LogRecordProcessor?>
    ): SdkLoggerProvider {
        val applicationName = environment.getProperty("spring.application.name", "application")
        val springResource = Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, applicationName))
        val builder = SdkLoggerProvider.builder()
            .setResource(Resource.getDefault().merge(springResource))
        logRecordProcessors.orderedStream().forEach { processor: LogRecordProcessor? ->
            builder.addLogRecordProcessor(
                processor!!
            )
        }
        return builder.build()
    }

    @Bean
    fun otelLogRecordProcessor(): LogRecordProcessor {
        return BatchLogRecordProcessor
            .builder(
                OtlpGrpcLogRecordExporter.builder()
                    .setEndpoint(loggingEndpoint!!)
                    .build()
            )
            .build()
    }
}
