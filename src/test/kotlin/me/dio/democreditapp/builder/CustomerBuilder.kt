package me.dio.democreditapp.builder

import me.dio.democreditapp.entity.Address
import me.dio.democreditapp.entity.Customer
import java.math.BigDecimal

class CustomerBuilder {
    companion object {
        fun buildCustomer(
            firstName: String = "Fulano",
            lastName: String = "de Tal",
            cpf: String = "86211190050",
            email: String = "fulanodetal@mail.com",
            password: String = "123456",
            zipCode: String = "12345000",
            street: String = "Rua do fulano de tal",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            id: Long? = 1L
        ) = Customer (
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                zipCode = zipCode,
                street = street
            ),
            income = income,
            id = id
        )
    }
}