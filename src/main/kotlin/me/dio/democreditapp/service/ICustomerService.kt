package me.dio.democreditapp.service

import me.dio.democreditapp.entity.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer
    fun findById(id: Long): Customer
    fun delete(id: Long): Customer
}