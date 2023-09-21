package code.vanilson.startup.service;

import code.vanilson.startup.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    /**
     * Returns the customer with the specified id.
     *
     * @param id ID of the customer to retrieve.
     * @return The requested Product if found.
     */
    Optional<Customer> findCustomerById(Long id);

    /**
     * Returns all products in the database.
     *
     * @return All products in the database.
     */
    List<Customer> findAllCustomers();

    /**
     * Updates the specified product, identified by its id.
     *
     * @param customer The customer to update.
     * @return True if the update succeeded, otherwise false.
     */
    Customer updateCustomer(long id, Customer customer);

    /**
     * Saves the specified customer to the database.
     *
     * @param customer The customer to save to the database.
     * @return The saved product.
     */
    Customer saveCustomer(Customer customer);

    /**
     * Deletes the customer with the specified id.
     *
     * @param id The id of the customer to delete.
     * @return True if the operation was successful.
     */
    boolean deleteCustomer(long id);
}
