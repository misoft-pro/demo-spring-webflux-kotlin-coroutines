package pro.misoft.poc.springreactive.kotlin.business.model.account

import pro.misoft.poc.springreactive.kotlin.business.model.common.IdGenerator
import java.math.BigDecimal

object AccountMother {
    fun newAccount(currency: String = "CHF", balance: Double): Account =
        Account(IdGenerator.uniqueNumber(), currency, BigDecimal.valueOf(balance), AccountType.FIAT)

    fun newAccount(currency: String = "CHF", balance: BigDecimal): Account =
        Account(IdGenerator.uniqueNumber(), currency, balance, AccountType.FIAT)
}