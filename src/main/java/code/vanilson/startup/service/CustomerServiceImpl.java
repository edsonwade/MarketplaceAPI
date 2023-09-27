package code.vanilson.startup.service;

import code.vanilson.startup.exception.IllegalRequestException;
import code.vanilson.startup.exception.ObjectWithIdNotFound;
import code.vanilson.startup.model.Customer;
import code.vanilson.startup.repository.CustomerRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    public static final String NOT_FOUND = " not found";
    public static final String THE_CUSTOMER_OBJECT_MUST_NOT_BE_NULL = "The 'customer' object must not be null.";
    private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> findAllCustomers() {
        logger.info(" All customers");
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findCustomerById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            throw new ObjectWithIdNotFound(String.format("customer with id %d not found", id));
        }
        logger.info("Customer with id:{}", customer.get() + "found");
        return customer;
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        if (Objects.isNull(customer)) {
            logger.error("The 'customer' object must not be:{}", customer);
            throw new IllegalRequestException(THE_CUSTOMER_OBJECT_MUST_NOT_BE_NULL);
        }
        logger.info("Customer saved with success:{}", customer);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(long id, Customer customer) {
        if (Objects.isNull(customer)) {
            logger.error(THE_CUSTOMER_OBJECT_MUST_NOT_BE_NULL);
            throw new IllegalRequestException(THE_CUSTOMER_OBJECT_MUST_NOT_BE_NULL);
        }
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            logger.error("Customer with id {} not found.", id);
            throw new ObjectWithIdNotFound("Customer with id " + id + " not found.");
        }

        var existingCustomer = optionalCustomer.get();

        if (customer.getName() == null || customer.getEmail() == null || customer.getAddress() == null) {
            logger.error("Updating to null values for 'name', 'email', or 'address' is not allowed.");
            throw new IllegalRequestException(
                    "Updating to null values for 'name', 'email', or 'address' is not allowed.");
        }

        existingCustomer.setName(customer.getName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setAddress(customer.getAddress());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        logger.info("Customer updated with success: {}", updatedCustomer);
        return updatedCustomer;
    }

    @Override
    @Transactional
    public boolean deleteCustomer(long id) {
        Optional<Customer> customers = customerRepository.findById(id);
        if (customers.isEmpty()) {
            throw new ObjectWithIdNotFound("customer with id " + id + NOT_FOUND);
        }
        customers.ifPresent(customerRepository::delete);
        logger.info("Delete customer with id: {}", id);
        return true;
    }
}
