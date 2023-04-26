package me.dio.democreditapp.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.democreditapp.entity.Address
import me.dio.democreditapp.entity.Customer
import me.dio.democreditapp.exception.BusinessException
import me.dio.democreditapp.repository.CustomerRepository
import me.dio.democreditapp.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.lang.Exception
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerService

    @Test
    fun `should create customer`() {
        //given
        val testCustomer: Customer = buildCustomer()
        every { customerRepository.save(any()) } returns testCustomer

        //when
        val actual: Customer = customerService.save(testCustomer)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(testCustomer)
        verify (exactly = 1){ customerRepository.save(testCustomer) }
    }

    @Test
    fun `should find customer by id`() {
        //given
        val testId: Long = Random().nextLong()
        val testCustomer: Customer = buildCustomer(id = testId)
        every { customerRepository.findById(testId) } returns Optional.of(testCustomer)

        //when
        val actual: Customer = customerService.findById(testId)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isExactlyInstanceOf(Customer::class.java)
        Assertions.assertThat(actual).isSameAs(testCustomer)
        verify(exactly = 1) { customerRepository.findById(testId) }
    }

    @Test
    fun `should not find customer by invalid id`() {
        //given
        val testId: Long = Random().nextLong()
        every { customerRepository.findById(testId) } returns Optional.empty()

        //when / then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { customerService.findById(testId) }
            .withMessage("Id $testId not found")

        verify(exactly = 1) { customerRepository.findById(testId) }
    }

    @Test
    fun `should delete customer by id`() {
        //given
        val testId: Long = Random().nextLong()
        val testCustomer: Customer = buildCustomer(id = testId)
        every { customerRepository.findById(testId) } returns Optional.of(testCustomer)
        every { customerRepository.delete(testCustomer) } just runs

        //when
        customerService.delete(testId)

        //then
        verify(exactly = 1) { customerRepository.findById(testId) }
        verify(exactly = 1) { customerRepository.delete(testCustomer) }
    }

    companion object {
        fun buildCustomer(
            firstName: String = "Fulano",
            lastName: String = "de Tal",
            cpf: String = "12345678910",
            email: String = "fulanodetal@mail.com",
            password: String = "123456",
            zipCode: String = "12345000",
            street: String = "Rua do fulano de tal",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            id: Long = 1L
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