package pro.misoft.poc.springreactive.kotlin.infra.finstar

import pro.misoft.poc.springreactive.kotlin.business.model.account.AccountType
import pro.misoft.poc.springreactive.kotlin.business.model.common.IdGenerator

object FsAccountMother {
    fun newAccount(currency: String): FsAccount {
        return FsAccount(IdGenerator.uniqueNumber(), currency, AccountType.FIAT.label)
    }
}