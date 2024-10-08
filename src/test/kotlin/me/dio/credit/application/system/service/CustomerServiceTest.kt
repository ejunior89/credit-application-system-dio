package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK
    lateinit var customerRepository: CustomerRepository
    @InjectMockKs
    lateinit var customerService: CustomerService

    @Test
    fun `should save a new customer`() {
        //given
        val fakeCustomer: Customer = buildCustomer()
        every { customerRepository.save(any()) } returns fakeCustomer
        //when
        val actual: Customer = customerService.save(fakeCustomer)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify ( exactly = 1 ) { customerRepository.save(fakeCustomer)}
    }

    @Test
    fun `should find a customer by id`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        //when
        val actual: Customer = customerService.findById(fakeId)

        //then
        Assertions.assertThat(actual).isExactlyInstanceOf(Customer::class.java)
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify ( exactly = 1 ) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should find a customer by invalid id and throw an BussinessException`() {
        //given
        val fakeId: Long = Random().nextLong()
        every { customerRepository.findById(fakeId) } returns Optional.empty()
        //when
        val exception = Assertions.catchThrowable { customerService.findById(fakeId) }

        //then
        Assertions.assertThat(exception).isExactlyInstanceOf(BusinessException::class.java)
        Assertions.assertThat(exception).hasMessage("Id $fakeId not found")
        verify ( exactly = 1 ) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should delete a customer by id`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs
        //when
        customerService.delete(fakeId)

        //then
        verify ( exactly = 1 ) { customerRepository.findById(fakeId) }
        verify ( exactly = 1 ) { customerRepository.delete(fakeCustomer) }
    }

    @Test
    fun `should throw an BussinessException when trying to delete a non-existent customer`() {
        //given
        val fakeId: Long = Random().nextLong()
        every { customerRepository.findById(fakeId) } returns Optional.empty()
        //when
        val exception = Assertions.catchThrowable { customerService.delete(fakeId) }

        //then
        Assertions.assertThat(exception).isExactlyInstanceOf(BusinessException::class.java)
        Assertions.assertThat(exception).hasMessage("Id $fakeId not found")
        verify ( exactly = 1 ) { customerRepository.findById(fakeId) }
        verify ( exactly = 0 ) { customerRepository.delete(any()) }
    }

    companion object {
        fun buildCustomer(
            firstName: String = "Elio",
            lastName: String = "Fernandes",
            cpf: String = "123456789",
            email: String = "elio@test.com",
            password: String = "12345",
            zipCode: String = "76900000",
            street: String = "Rua Teste",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            id: Long = 1L
        ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                zipCode = zipCode,
                street = street,
            ),
            income = income,
            id = id,
        )
    }
}