package pro.misoft.poc.springreactive.kotlin.infra.spring.tracing

import org.springframework.boot.actuate.web.exchanges.HttpExchange
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class InMemoryHttpExchangeRepository : HttpExchangeRepository {
    private var capacity = 100

    private var reverse = true

    private val httpExchanges: MutableList<HttpExchange> = LinkedList()

    /**
     * Flag to say that the repository lists exchanges in reverse order.
     * @param reverse flag value (default true)
     */
    fun setReverse(reverse: Boolean) {
        synchronized(this.httpExchanges) {
            this.reverse = reverse
        }
    }

    /**
     * Set the capacity of the in-memory repository.
     * @param capacity the capacity
     */
    fun setCapacity(capacity: Int) {
        synchronized(this.httpExchanges) {
            this.capacity = capacity
        }
    }

    override fun findAll(): List<HttpExchange> {
        synchronized(this.httpExchanges) {
            return java.util.List.copyOf(this.httpExchanges)
        }
    }

    override fun add(exchange: HttpExchange) {
        synchronized(this.httpExchanges) {
            while (this.httpExchanges.size >= this.capacity) {
                this.httpExchanges.removeAt(if (this.reverse) this.capacity - 1 else 0)
            }
            if (!exchange.request.uri.path.contains("/actuator")) {
                if (this.reverse) {
                    this.httpExchanges.add(0, exchange)
                } else {
                    this.httpExchanges.add(exchange)
                }
            }
        }
    }
}