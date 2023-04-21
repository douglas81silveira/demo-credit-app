package me.dio.democreditapp.dto

import me.dio.democreditapp.entity.Credit
import me.dio.democreditapp.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto (
    val creditValue: BigDecimal,
    val dayFirstInstallment: LocalDate,
    val numberOfInstallments: Int,
    val customerId: Long
) {
    fun toEntity (): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallment = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}
