package pro.misoft.poc.springreactive.kotlin.infra.serializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.javamoney.moneta.Money
import java.io.IOException
import javax.money.MonetaryAmount


object CustomMonetaryAmountDeserializer : JsonDeserializer<MonetaryAmount>() {

    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Money {
        val node = p.codec.readTree<JsonNode>(p)
        val currency = node["currency"].asText()
        val amount = node["amount"].asDouble()
        return Money.of(amount, currency)
    }
}
