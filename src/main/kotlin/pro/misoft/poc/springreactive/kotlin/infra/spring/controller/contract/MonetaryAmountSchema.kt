package pro.misoft.poc.springreactive.kotlin.infra.spring.controller.contract

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(name = "MonetaryAmount", description = "Monetary amount with currency and formatted value")
data class MonetaryAmountSchema(

    @Schema(description = "Currency code", example = "USD")
    val currency: String,
    @Schema(description = "Amount value", example = "100.99")
    val amount: BigDecimal,
    @Schema(description = "Locale formatted amount with currency symbol", example = "$ 100.99")
    val formatted: String
)