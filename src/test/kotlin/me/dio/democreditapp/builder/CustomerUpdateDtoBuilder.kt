package me.dio.democreditapp.builder

import me.dio.democreditapp.dto.CustomerDto
import me.dio.democreditapp.dto.CustomerUpdateDto
import java.math.BigDecimal

class CustomerUpdateDtoBuilder {
    companion object {
        fun buildCustomerUpdateDto(
            firstName: String = "Fulano",
            lastName: String = "Beltrano da Silva",
            zipCode: String = "10200300",
            street: String = "Rua do fulano de tal atualizada",
            income: BigDecimal = BigDecimal.valueOf(10000.0)
        ) = CustomerUpdateDto(
            firstName = firstName,
            lastName = lastName,
            zipCode = zipCode,
            street = street,
            income = income
        )
    }
}