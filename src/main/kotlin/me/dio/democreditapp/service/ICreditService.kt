package me.dio.democreditapp.service

import me.dio.democreditapp.entity.Credit
import java.util.UUID

interface ICreditService {
    fun save(credit: Credit): Credit
    fun findAllByCustomerId(customerId: Long): List<Credit>
    fun findByCreditCode(customerId:Long, creditCode: UUID): Credit
}