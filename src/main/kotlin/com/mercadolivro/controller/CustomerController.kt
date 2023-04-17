package com.mercadolivro.controller

import com.mercadolivro.controller.request.PostCustomerRequest
import com.mercadolivro.controller.request.PutCustomerRequest
import com.mercadolivro.controller.response.CustomerResponse
import com.mercadolivro.extension.toCustomerModel
import com.mercadolivro.extension.toCustomerResponse
import com.mercadolivro.security.UserCanOnlyAccessTheirOwnResources
import com.mercadolivro.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("customers")
class CustomerController(
    private val customerService: CustomerService
) {
    @GetMapping
    @UserCanOnlyAccessTheirOwnResources
    fun getAllCustomers(@RequestParam name: String?): List<CustomerResponse> {
        return customerService.getCustomers(name).map { it.toCustomerResponse() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCustomer(@RequestBody @Valid customer: PostCustomerRequest) {
        customerService.createCustomer(customer.toCustomerModel())
    }

    @GetMapping("/{id}")
    @UserCanOnlyAccessTheirOwnResources
    fun getCustomer(@PathVariable id: Int): CustomerResponse {
        return customerService.findById(id).toCustomerResponse()
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @UserCanOnlyAccessTheirOwnResources
    fun putCustomer(@PathVariable id: Int, @RequestBody @Valid customer: PutCustomerRequest) {
        val customerSaved = customerService.findById(id)
        customerService.putCustomer(customer.toCustomerModel(customerSaved))
    }

    @DeleteMapping("/{id}")
    @UserCanOnlyAccessTheirOwnResources
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCustomer(@PathVariable id: Int) {
        customerService.deleteCustomer(id)
    }
}