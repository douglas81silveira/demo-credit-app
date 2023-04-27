package me.dio.democreditapp.builder

import me.dio.democreditapp.dto.CreditDto
import me.dio.democreditapp.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

class CreditDtoBuilder {
    companion object {
        fun buildCreditDto(
            creditValue:BigDecimal = BigDecimal.valueOf(50000.0),
            dayFirstInstallment:LocalDate = LocalDate.now().plusMonths(1),
            numberOfInstallments:Int = 60,
            customerId:Long = 1L
        ) = CreditDto(
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            customerId = customerId
        )
    }
}