package pro.misoft.poc.springreactive.kotlin.infra.serializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import pro.misoft.poc.springreactive.kotlin.infra.spring.filter.LocaleThreadLocalAccessor
import org.javamoney.moneta.Money
import javax.money.format.MonetaryAmountFormat
import javax.money.format.MonetaryFormats

object LocaleAwareMoneyDeserializer : JsonDeserializer<Money>() {

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Money {
        val node = jsonParser.codec.readTree<com.fasterxml.jackson.databind.node.TextNode>(jsonParser)
        val formatter: MonetaryAmountFormat = MonetaryFormats.getAmountFormat(LocaleThreadLocalAccessor.getValue())
        return formatter.parse(node.asText()).let { it as Money }
    }
}
