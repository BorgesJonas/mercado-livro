package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Errors
import com.mercadolivro.enums.Role
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.helper.buildCostumer
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
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
    @SpyK
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
    fun `should throw error when customer not found by id`() {
        val id = Random().nextInt()

        every { customerRepository.findById(id) } returns Optional.empty()

        val error = assertThrows<NotFoundException>{ customerService.findById(id) }

        assertEquals("Customer with the id [${id}] doesn't exist", error.message)
        assertEquals("ML-1102", error.errorCode)
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    @Test
    fun `should update customer`() {
        val id = Random().nextInt()
        val fakeCustomer = buildCostumer(id = id)

        every { customerRepository.existsById(id) } returns true
        every { customerRepository.save(fakeCustomer) } returns fakeCustomer

        customerService.putCustomer(fakeCustomer)

        verify(exactly = 1) { customerRepository.existsById(id) }
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun `should throw error on update when user doesn't exist by id`() {
        val id = Random().nextInt()
        val fakeCustomer = buildCostumer(id = id)

        every { customerRepository.existsById(id) } returns false

        val error = assertThrows<NotFoundException>{ customerService.putCustomer(fakeCustomer) }

        assertEquals("Customer with the id [${id}] doesn't exist", error.message)
        assertEquals("ML-1102", error.errorCode)
        verify(exactly = 1) { customerRepository.existsById(id) }
        verify(exactly = 0) { customerRepository.save(any()) }

    }

    @Test
    fun `should delete customer`() {
        val id = Random().nextInt()
        val fakeCustomer = buildCostumer(id = id)
        val expetedCustomer = fakeCustomer.copy(status = CustomerStatus.INATIVO)

        every { customerService.findById(id) } returns fakeCustomer
        every { bookService.deleteByCustomer(fakeCustomer) } just runs
        every { customerRepository.save(expetedCustomer) } returns expetedCustomer

        customerService.deleteCustomer(id)

        verify(exactly = 1) { customerService.findById(id) }
        verify(exactly = 1) { bookService.deleteByCustomer(fakeCustomer) }
        verify(exactly = 1) { customerRepository.save(expetedCustomer) }
    }

    @Test
    fun `should throw not found exception when delete customer`() {
        val id = Random().nextInt()

        every { customerService.findById(id) }throws NotFoundException(Errors.ML1102.message.format(id), Errors.ML1102.code)

        val error = assertThrows<NotFoundException>{ customerService.deleteCustomer(id) }

        assertEquals("Customer with the id [${id}] doesn't exist", error.message)
        assertEquals("ML-1102", error.errorCode)

        verify(exactly = 1) { customerService.findById(id) }
        verify(exactly = 0) { bookService.deleteByCustomer(any()) }
        verify(exactly = 0) { bookService.deleteByCustomer(any()) }
    }

    @Test
    fun `should return true when email available`() {
        val email = "${Random().nextInt().toString()}@email.com"

        every {  customerRepository.existsByEmail(email) } returns false

        val emailAvailable = customerService.emailAvailable(email)

        assertTrue(emailAvailable)
        verify(exactly = 1) { customerRepository.existsByEmail(email) }
    }

    @Test
    fun `should return true when email unavailable`() {
        val email = "${Random().nextInt().toString()}@email.com"

        every {  customerRepository.existsByEmail(email) } returns true

        val emailAvailable = customerService.emailAvailable(email)

        assertFalse(emailAvailable)
        verify(exactly = 1) { customerRepository.existsByEmail(email) }
    }
}