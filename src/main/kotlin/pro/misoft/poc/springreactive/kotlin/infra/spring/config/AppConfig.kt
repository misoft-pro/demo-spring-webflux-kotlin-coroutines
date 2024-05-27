package pro.misoft.poc.springreactive.kotlin.infra.spring.config

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micrometer.context.ContextRegistry
import jakarta.annotation.PostConstruct
import jakarta.validation.Validator
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.AbstractResourceBasedMessageSource
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import pro.misoft.poc.springreactive.kotlin.business.model.currency.CryptoCurrencies
import pro.misoft.poc.springreactive.kotlin.infra.serializer.CustomMonetaryAmountDeserializer
import pro.misoft.poc.springreactive.kotlin.infra.serializer.LocaleAwareMoneySerializer
import pro.misoft.poc.springreactive.kotlin.infra.spring.controller.contract.MonetaryAmountSchema
import pro.misoft.poc.springreactive.kotlin.infra.spring.filter.LocaleThreadLocalAccessor
import reactor.core.publisher.Hooks
import javax.money.MonetaryAmount


@Configuration
class AppConfig {

    @Bean
    fun messageSource(): AbstractResourceBasedMessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("i18n/messages")
        messageSource.setCacheSeconds(3600) // Cache messages for 1 hour
        return messageSource
    }

    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { jacksonObjectMapperBuilder ->
            val customMoneyModule = SimpleModule().apply {
                addSerializer(MonetaryAmount::class.java, LocaleAwareMoneySerializer)
                addDeserializer(MonetaryAmount::class.java, CustomMonetaryAmountDeserializer)
            }
            jacksonObjectMapperBuilder
                .modules(KotlinModule.Builder().build(), customMoneyModule)
        }
    }

    @PostConstruct
    fun init() {
        Hooks.enableAutomaticContextPropagation()
        ContextRegistry.getInstance().registerThreadLocalAccessor(LocaleThreadLocalAccessor)
        CryptoCurrencies.initialize()
        SpringDocUtils.getConfig().replaceWithClass(
            MonetaryAmount::class.java, MonetaryAmountSchema::class.java
        )
    }

    @Bean
    fun localisedConstraintValidator(messageSource: AbstractResourceBasedMessageSource): Validator {
        val bean = LocalValidatorFactoryBean()
        bean.setValidationMessageSource(messageSource)
        return bean
    }
}