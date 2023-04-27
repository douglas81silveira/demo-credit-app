package me.dio.democreditapp.builder

import me.dio.democreditapp.builder.CustomerBuilder.Companion.buildCustomer
import me.dio.democreditapp.entity.Credit
import me.dio.democreditapp.entity.Customer
import me.dio.democreditapp.enummeration.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class CreditBuilder {
    companion object {
        fun buildCredit(
            creditCode: UUID = UUID.randomUUID(),
            creditValue: BigDecimal = BigDecimal.valueOf(50000.0),
            dayFirstInstallment: LocalDate = LocalDate.now(),
            numberOfInstallments: Int = 60,
            status: Status = Status.IN_PROGRESS,
            customer: Customer = buildCustomer(),
            id: Long? = 1L
        ) = Credit(
            creditCode = creditCode,
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            status = status,
            customer = customer,
            id = id
        )
    }
}