package me.dio.democreditapp.controller

import me.dio.democreditapp.dto.CustomerDto
import me.dio.democreditapp.dto.CustomerUpdateDto
import me.dio.democreditapp.dto.CustomerView
import me.dio.democreditapp.entity.Customer
import me.dio.democreditapp.service.impl.CustomerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customers")
class CustomerController(
    private val customerService: CustomerService
) {
    @PostMapping
    fun saveCustomer(@RequestBody customerDto: CustomerDto): String {
        val savedCustomer = this.customerService.save(customerDto.toEntity())
        return "Customer ${savedCustomer.email} saved!"
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): CustomerView = CustomerView(this.customerService.findById(id))

    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: Long) = this.customerService.delete(id)

    @PatchMapping
    fun updateCustomer(@RequestParam(value = "customerId") id: Long,
                       @RequestBody customerUpdateDto: CustomerUpdateDto): CustomerView {

        val customer: Customer = this.customerService.findById(id)
        val customerUpdated: Customer = this.customerService.save(customerUpdateDto.toEntity(customer))
        return CustomerView(customerUpdated)
    }
}