package pro.misoft.poc.springreactive.kotlin.infra.spring.tracing

import io.micrometer.tracing.CurrentTraceContext
import io.micrometer.tracing.TraceContext
import kotlinx.coroutines.ThreadContextElement
import org.slf4j.MDC
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class MdcContextElement(
    private val traceContext: TraceContext?
) : ThreadContextElement<Unit>, AbstractCoroutineContextElement(MdcContextElement) {

    companion object Key : CoroutineContext.Key<MdcContextElement>

    override fun updateThreadContext(context: CoroutineContext) {
        traceContext?.let {
            MDC.put("traceId", it.traceId())
            MDC.put("spanId", it.spanId())
        }
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Unit) {
        MDC.remove("traceId")
        MDC.remove("spanId")
    }
}

fun CurrentTraceContext.asMdcCoroutineContext(): CoroutineContext {
    val traceContext = this.context()
    return MdcContextElement(traceContext)
}
