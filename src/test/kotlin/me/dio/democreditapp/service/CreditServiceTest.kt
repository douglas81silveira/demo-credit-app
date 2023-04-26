package me.dio.democreditapp.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.democreditapp.entity.Credit
import me.dio.democreditapp.entity.Customer
import me.dio.democreditapp.enummeration.Status
import me.dio.democreditapp.exception.BusinessException
import me.dio.democreditapp.repository.CreditRepository
import me.dio.democreditapp.service.CustomerServiceTest.Companion.buildCustomer
import me.dio.democreditapp.service.impl.CreditService
import me.dio.democreditapp.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @MockK lateinit var customerService: CustomerService
    @InjectMockKs lateinit var creditService: CreditService

    @Test
    fun `should create credit`(){
        //given
        val testCredit = buildCredit()
        every { customerService.findById(testCredit.customer?.id!!) } returns testCredit.customer!!
        every { creditRepository.save(any()) } returns testCredit

        //when
        val actual:Credit = creditService.save(testCredit)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(testCredit)
        verify (exactly = 1){ creditRepository.save(testCredit) }
    }

    @Test
    fun `should find all credit by customer id`() {
        //given
        val testCustomerId: Long = Random().nextLong()
        val creditList: List<Credit> = listOf(buildCredit(), buildCredit(), buildCredit())
        every { creditRepository.findAllByCustomerId(testCustomerId) } returns creditList

        //when
        val actual:List<Credit> = creditService.findAllByCustomerId(testCustomerId)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isNotEmpty
        Assertions.assertThat(actual).isSameAs(creditList)
        verify (exactly = 1){ creditRepository.findAllByCustomerId(testCustomerId) }
    }

    @Test
    fun `should find credit by credit code`() {
        //given
        val testCreditCode: UUID = UUID.randomUUID()
        val testCutomerId: Long = Random().nextLong()
        val testCredit: Credit = buildCredit(
            creditCode = testCreditCode,
            customer =  buildCustomer(id = testCutomerId))

        every { creditRepository.findByCreditCode(testCreditCode) } returns testCredit

        // when
        val actual: Credit = creditService.findByCreditCode(testCutomerId, testCreditCode)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(testCredit)
        verify (exactly = 1){ creditRepository.findByCreditCode(testCreditCode) }
    }

    @Test
    fun `should not find credit by credit code`() {
        //given
        val testCreditCode: UUID = UUID.randomUUID()
        val testCutomerId: Long = Random().nextLong()

        every { creditRepository.findByCreditCode(testCreditCode) } returns null

        // when then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(testCutomerId, testCreditCode) }
            .withMessage("CreditCode $testCreditCode not found!")
    }

    companion object {
        fun buildCredit(
            creditCode: UUID = UUID.randomUUID(),
            creditValue: BigDecimal = BigDecimal.valueOf(50000.0),
            dayFirstInstallment: LocalDate = LocalDate.now(),
            numberOfInstallments: Int = 60,
            status: Status = Status.IN_PROGRESS,
            customer: Customer = CustomerServiceTest.buildCustomer(),
            id: Long = 1L
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