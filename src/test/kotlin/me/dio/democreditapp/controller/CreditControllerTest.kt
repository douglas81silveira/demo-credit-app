package me.dio.democreditapp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.democreditapp.builder.CreditBuilder.Companion.buildCredit
import me.dio.democreditapp.builder.CreditDtoBuilder
import me.dio.democreditapp.builder.CustomerBuilder.Companion.buildCustomer
import me.dio.democreditapp.dto.CreditDto
import me.dio.democreditapp.entity.Credit
import me.dio.democreditapp.entity.Customer
import me.dio.democreditapp.repository.CreditRepository
import me.dio.democreditapp.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditControllerTest {
    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }
    @AfterEach
    fun tearDown() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val creditDto: CreditDto = CreditDtoBuilder.buildCreditDto(customerId = customer.id!!)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value("50000.0"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not create credit by invalid dayFirstInstallment and return 400 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val creditDto: CreditDto = CreditDtoBuilder.buildCreditDto(customerId = customer.id!!, dayFirstInstallment = LocalDate.now())
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request. Consult the documentation!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not create credit by invalid customerId and return 400 status`() {
        //given
        val invalidId:Long = Random().nextLong()
        val creditDto: CreditDto = CreditDtoBuilder.buildCreditDto(customerId = invalidId)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request. Consult the documentation!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.democreditapp.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find all credits by customerId`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        creditRepository.save(buildCredit(customer = customer, creditValue = BigDecimal.valueOf(25)))
        creditRepository.save(buildCredit(customer = customer, creditValue = BigDecimal.valueOf(50)))

        //when //then
        mockMvc.perform(MockMvcRequestBuilders
            .get(URL)
            .param("customerId", "${customer.id}")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credits by nonexistent customerId`() {
        //given
        val nonexistentId:Long = Random().nextLong()
        //when //then
        mockMvc.perform(MockMvcRequestBuilders
            .get(URL)
            .param("customerId", "$nonexistentId")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find credit by creditCode`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val credit: Credit = creditRepository.save(buildCredit(
            customer = customer,
            creditValue = BigDecimal.valueOf(25),
            numberOfInstallments = 10,
        ))

        //when //then
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL/${credit.creditCode}")
            .param("customerId","${customer.id}")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(25))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credit by invalid creditCode`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val invalidCreditCode:UUID = UUID.randomUUID()

        //when //then
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL/$invalidCreditCode")
            .param("customerId","${customer.id}")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.democreditapp.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credit by invalid customerId`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val credit: Credit = creditRepository.save(buildCredit(
            customer = customer,
            creditValue = BigDecimal.valueOf(25),
            numberOfInstallments = 10,
        ))

        //when //then
        mockMvc.perform(MockMvcRequestBuilders
            .get("$URL/${credit.creditCode}")
            .param("customerId","${Random().nextLong()}")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.democreditapp.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }
}