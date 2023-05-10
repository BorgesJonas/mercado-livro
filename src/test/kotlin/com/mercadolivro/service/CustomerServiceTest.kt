package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Role
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    private lateinit var customerRepository: CustomerRepository

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var bCrypt: BCryptPasswordEncoder

    @InjectMockKs
    private lateinit var customerService: CustomerService

    @Test
    fun `should return all customers`() {
        val fakeCostumers = listOf(buildCostumer(), buildCostumer())

        every { customerRepository.findAll() } returns fakeCostumers

        val customers = customerService.getCustomers(null)

        assertEquals(fakeCostumers, customers)
        verify(exactly = 1) { customerRepository.findAll() }
        verify(exactly = 0) { customerRepository.findByNameContaining(any()) }
    }

    @Test
    fun `should return customers when named is informed`() {
        val name = UUID.randomUUID().toString()
        val fakeCostumers = listOf(buildCostumer(), buildCostumer())

        every { customerRepository.findByNameContaining(name) } returns fakeCostumers

        val customers = customerService.getCustomers(name)

        assertEquals(fakeCostumers, customers)
        verify(exactly = 1) { customerRepository.findByNameContaining(any()) }
        verify(exactly = 0) { customerRepository.findAll() }
    }

    @Test
    fun `should create customer and encrypt password`() {
        val initialPassword = Random().nextInt().toString()
        val fakeCustomer = buildCostumer(password = initialPassword)
        val fakePassword = UUID.randomUUID().toString()
        val fakeCustomerWithEncryptedPassword = fakeCustomer.copy(password = fakePassword)

        every { customerRepository.save(fakeCustomerWithEncryptedPassword) } returns fakeCustomer
        every { bCrypt.encode(initialPassword) } returns fakePassword

        customerService.createCustomer(fakeCustomer)

        verify(exactly = 1) { customerRepository.save(fakeCustomerWithEncryptedPassword) }
        verify(exactly = 1) { bCrypt.encode(initialPassword) }
    }

    @Test
    fun `should return customer by id`() {
        val id = Random().nextInt()
        val fakeCustomer = buildCostumer(id = id)

        every { customerRepository.findById(id) } returns Optional.of(fakeCustomer)

        val customer = customerService.findById(id)

        assertEquals(fakeCustomer, customer)
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    @Test
    fun `should throw error when user not found`() {
        val id = Random().nextInt()

        every { customerRepository.findById(id) } returns Optional.empty()

        val error = assertThrows<NotFoundException>{ customerService.findById(id) }

        assertEquals("Customer with the id [${id}] doesn't exist", error.message)
        assertEquals("ML-1102", error.errorCode)
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    fun buildCostumer(
        id: Int? = null,
        name: String = "Customer Name",
        email: String = "${UUID.randomUUID()}@email.com",
        password: String = "password"
    ) = CustomerModel(
        id = id,
        name = name,
        email = email,
        password = password,
        status = CustomerStatus.ATIVO,
        roles = setOf(Role.CUSTOMER)
    )
}