package pro.misoft.poc.springreactive.kotlin.infra.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import pro.misoft.poc.springreactive.kotlin.business.model.currency.CustomMoneyFormatter
import pro.misoft.poc.springreactive.kotlin.infra.spring.filter.LocaleThreadLocalAccessor
import javax.money.MonetaryAmount


object LocaleAwareMoneySerializer : JsonSerializer<MonetaryAmount>() {
    override fun serialize(money: MonetaryAmount, gen: JsonGenerator, serializers: SerializerProvider) {
        val locale = LocaleThreadLocalAccessor.getValue()
        val currency = money.currency
        val formatter = CustomMoneyFormatter(locale, currency.currencyCode)
        val formattedAmount = formatter.format(money)

        gen.writeStartObject()
        gen.writeStringField("currency", currency.currencyCode)
        gen.writeNumberField("amount", money.number.doubleValueExact())
        gen.writeStringField("formatted", formattedAmount)
//        gen.writeStringField("currencySymbol", formatter.parse(formattedAmount).currency.currencyCode)
        gen.writeEndObject()
    }
}
