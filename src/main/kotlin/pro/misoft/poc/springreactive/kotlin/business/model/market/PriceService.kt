package pro.misoft.poc.springreactive.kotlin.business.model.market

import java.math.BigDecimal

interface PriceService {
    suspend fun quote(fromCurrency: String, toCurrency: String): BigDecimal
}
