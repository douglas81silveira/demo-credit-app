package me.dio.democreditapp.entity

import me.dio.democreditapp.enummeration.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class Credit(
    val creditCode: UUID = UUID.randomUUID(),
    val creditValue: BigDecimal = BigDecimal.ZERO,
    val dayFirstInstallment: LocalDate,
    val numberOfInstallment: Int = 0,
    val status: Status = Status.IN_PROGRESS,
    val customer: Customer? = null,
    val id: Long? = null
)