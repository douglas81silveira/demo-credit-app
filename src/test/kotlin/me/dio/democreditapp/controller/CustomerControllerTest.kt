package me.dio.democreditapp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.democreditapp.builder.CustomerBuilder.Companion.buildCustomer
import me.dio.democreditapp.builder.CustomerDtoBuilder.Companion.buildCustomerDto
import me.dio.democreditapp.builder.CustomerUpdateDtoBuilder.Companion.buildCustomerUpdateDto
import me.dio.democreditapp.dto.CustomerDto
import me.dio.democreditapp.dto.CustomerUpdateDto
import me.dio.democreditapp.entity.Customer
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
import java.util.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerControllerTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach fun setup() = customerRepository.deleteAll()
    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status`() {
        //given
        val customerDto: CustomerDto = buildCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)

        //when //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("86211190050"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with same CPF and return 409 status`() {
        //given
        customerRepository.save(buildCustomer())
        val customerDto: CustomerDto = buildCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)

        //when then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.exception").value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `shoud not save a customer with firstname empty and return 400 status`() {
        //given
        val customerDto: CustomerDto = buildCustomerDto(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)

        //when then
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return 200 status` () {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())

        //when then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("86211190050"))
            .andDo(MockMvcResultHandlers.print())
    }
    @Test
    fun `should not find customer by invalid id and return 400 status` () {
        //given
        val invalidId: Long = 2L

        // when //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/$invalidId")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.exception").value("class me.dio.democreditapp.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete customer by id`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete customer by id and return 400 status`() {
        //given
        val invalidId: Long = Random().nextLong()

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/$invalidId")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.exception").value("class me.dio.democreditapp.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update customer and return 200`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val customerUpdateDto: CustomerUpdateDto = buildCustomerUpdateDto()
        val valueAsString = objectMapper.writeValueAsString(customerUpdateDto)

        //when then
        mockMvc.perform(MockMvcRequestBuilders.patch(URL)
            .param("customerId", "${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Fulano"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Beltrano da Silva"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("10200300"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("10000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua do fulano de tal atualizada"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should no update customer and return 400`() {
        val invalidId:Long = Random().nextLong()
        val customerUpdateDto: CustomerUpdateDto = buildCustomerUpdateDto()
        val valueAsString = objectMapper.writeValueAsString(customerUpdateDto)

        //when then
        mockMvc.perform(MockMvcRequestBuilders.patch(URL)
            .param("customerId", "$invalidId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.exception").value("class me.dio.democreditapp.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }
}