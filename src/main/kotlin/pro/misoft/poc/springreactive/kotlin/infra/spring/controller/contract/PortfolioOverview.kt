package pro.misoft.poc.springreactive.kotlin.infra.spring.controller.contract

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import pro.misoft.poc.springreactive.kotlin.business.model.portfolio.PortfolioItem
import javax.money.MonetaryAmount

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PortfolioOverview(
    @JsonProperty("items") val items: List<PortfolioItem>,
    @JsonProperty("totalMarketValue") val totalMarketValue: MonetaryAmount
)
