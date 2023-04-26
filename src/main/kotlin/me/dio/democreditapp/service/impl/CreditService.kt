package me.dio.democreditapp.service.impl

import me.dio.democreditapp.entity.Credit
import me.dio.democreditapp.exception.BusinessException
import me.dio.democreditapp.repository.CreditRepository
import me.dio.democreditapp.service.ICreditService
import org.springframework.stereotype.Service
import java.util.*
@Service
class CreditService (
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
    ): ICreditService {

    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomerId(customerId: Long): List<Credit> =
        creditRepository.findAllByCustomerId(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = (this.creditRepository.findByCreditCode(creditCode)
            ?: throw BusinessException("CreditCode $creditCode not found!"))

        return if (credit.customer?.id == customerId) credit else throw BusinessException("Contact admin")
    }
}