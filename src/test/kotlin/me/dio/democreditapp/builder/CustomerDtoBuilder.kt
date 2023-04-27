package me.dio.democreditapp.builder

import me.dio.democreditapp.dto.CustomerDto
import me.dio.democreditapp.entity.Address
import java.math.BigDecimal

class CustomerDtoBuilder {
    companion object {
        fun buildCustomerDto(
            firstName: String = "Fulano",
            lastName: String = "de Tal",
            cpf: String = "86211190050",
            email: String = "fulanodetal@mail.com",
            password: String = "123456",
            zipCode: String = "12345000",
            street: String = "Rua do fulano de tal",
            income: BigDecimal = BigDecimal.valueOf(1000.0)
        ) = CustomerDto(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            zipCode = zipCode,
            street = street,
            income = income
        )
    }
}