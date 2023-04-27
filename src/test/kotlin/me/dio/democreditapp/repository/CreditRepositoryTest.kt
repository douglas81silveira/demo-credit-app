package me.dio.democreditapp.repository

import me.dio.democreditapp.builder.CreditBuilder.Companion.buildCredit
import me.dio.democreditapp.builder.CustomerBuilder
import me.dio.democreditapp.builder.CustomerBuilder.Companion.buildCustomer
import me.dio.democreditapp.entity.Address
import me.dio.democreditapp.entity.Credit
import me.dio.democreditapp.entity.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.UUID

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired
    lateinit var creditRepository: CreditRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit

    @BeforeEach
    fun setup(){
        customer = testEntityManager.persist(buildCustomer( id = null))
        credit1 = testEntityManager.persist(buildCredit(id = null, customer = customer))
        credit2 = testEntityManager.persist(buildCredit(id = null, customer = customer))
    }

    @Test
    fun `should find credit by credit code`() {
        //given
        val creditCode1 = UUID.fromString("67064fc6-623a-4741-aa5f-c9519774dde7")
        val creditCode2 = UUID.fromString("76e9774a-8574-4fd2-b9cf-fb4db19db4aa")
        credit1.creditCode = creditCode1
        credit2.creditCode = creditCode2

        //when
        val testCredit1: Credit = creditRepository.findByCreditCode(creditCode1)!!
        val testCredit2: Credit = creditRepository.findByCreditCode(creditCode2)!!

        //then
        Assertions.assertThat(testCredit1).isNotNull
        Assertions.assertThat(testCredit2).isNotNull
        Assertions.assertThat(testCredit1).isSameAs(credit1)
        Assertions.assertThat(testCredit2).isSameAs(credit2)
    }

    @Test
    fun `should find credits by customer id`() {
        //given
        val testCustomerId:Long = 1L

        //when
        val creditList: List<Credit> = creditRepository.findAllByCustomerId(testCustomerId)

        //then
        Assertions.assertThat(creditList).isNotEmpty
        Assertions.assertThat(creditList.size).isEqualTo(2)
        Assertions.assertThat(creditList).contains(credit1, credit2)
    }
}