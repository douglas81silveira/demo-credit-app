package me.dio.democreditapp.dto

import me.dio.democreditapp.entity.Credit
import java.math.BigDecimal
import java.util.*

data class CreditViewListItem(
    val creditCode: UUID,
    val creditValue: BigDecimal,
    val numberOfInstallments: Int
    ) {
    constructor(credit: Credit): this (
        creditCode = credit.creditCode,
        creditValue = credit.creditValue,
        numberOfInstallments = credit.numberOfInstallments
    )
}
