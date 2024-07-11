package pro.misoft.poc.springreactive.kotlin.infra.spring.tracing

import io.micrometer.tracing.CurrentTraceContext
import io.micrometer.tracing.TraceContext
import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class TracingContextElement(
    private val currentTraceContext: CurrentTraceContext,
    private val traceContext: TraceContext?
) : ThreadContextElement<CurrentTraceContext.Scope?>, AbstractCoroutineContextElement(Key) {

    companion object Key : CoroutineContext.Key<TracingContextElement>

    override fun updateThreadContext(context: CoroutineContext): CurrentTraceContext.Scope? {
        return traceContext?.let { currentTraceContext.newScope(it) }
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: CurrentTraceContext.Scope?) {
        oldState?.close()
    }
}

fun CurrentTraceContext.asCoroutineContext(): CoroutineContext {
    val traceContext = this.context()
    return TracingContextElement(this, traceContext)
}


