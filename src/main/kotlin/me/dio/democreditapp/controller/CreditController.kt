package me.dio.democreditapp.controller

import me.dio.democreditapp.dto.CreditDto
import me.dio.democreditapp.dto.CreditView
import me.dio.democreditapp.dto.CreditViewListItem
import me.dio.democreditapp.entity.Credit
import me.dio.democreditapp.service.impl.CreditService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/credits")
class CreditController(
    private val creditService: CreditService
) {
    @PostMapping
    fun saveCredit(@RequestBody creditDto: CreditDto): String {
        val credit: Credit = this.creditService.save(creditDto.toEntity())
        return "Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!"
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): List<CreditViewListItem> {
        return this.creditService.findAllByCustomerId(customerId).stream().map {
            credit: Credit -> CreditViewListItem(credit)
        }.toList()
    }

    @GetMapping
    fun findByCreditCode(@RequestParam(value = "customerId") customerId: Long,
                         @PathVariable creditCode: UUID): CreditView {
        return CreditView(this.creditService.findByCreditCode(customerId, creditCode))
    }
}