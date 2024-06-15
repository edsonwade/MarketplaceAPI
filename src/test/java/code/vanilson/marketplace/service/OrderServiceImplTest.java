package code.vanilson.marketplace.service;

import code.vanilson.marketplace.exception.IllegalRequestException;
import code.vanilson.marketplace.exception.ObjectWithIdNotFound;
import code.vanilson.marketplace.model.Customer;
import code.vanilson.marketplace.model.Order;
import code.vanilson.marketplace.model.OrderItem;
import code.vanilson.marketplace.model.Product;
import code.vanilson.marketplace.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    /**
     * List of Orders
     */
    public static List<Order> orders;
    /**
     * Mock OrderRepository
     */
    OrderRepository orderRepositoryMock;
    /**
     * Current Object OrderServiceImpl
     */
    OrderServiceImpl currentInstance;
    /**
     * Object Order
     */
    Order order;
    /**
     * Customer
     */
    Customer customer;
    /**
     * OrderItem
     */
    Set<OrderItem> orderItem;

    @BeforeEach
    void setUp() {
        orderRepositoryMock = mock(OrderRepository.class);
        currentInstance = new OrderServiceImpl(orderRepositoryMock);

        customer = new Customer(1L, "test", "test@test.test", "test 1");
        orderItem = Set.of(new OrderItem(1L, order, new Product(), 45));
        order = new Order(1L, LocalDateTime.now(), customer, new HashSet<>());
        orders = List.of(
                new Order(1L, LocalDateTime.now(), customer, orderItem)
        );
    }

    @Test
    @DisplayName("Get all Orders")
    void testGetAllOrders() {
        //when
        when(orderRepositoryMock.findAllOrdersWithDetails()).thenReturn(orders);
        var currentActual = currentInstance.findAllOrders();

        //Asserts
        assertEquals(orders, currentActual);
        assertFalse(currentActual.isEmpty());
        assertNotNull(currentActual);
        //verify
        verify(orderRepositoryMock, times(1)).findAllOrdersWithDetails();
    }

    @Test
    @DisplayName("Get Order by id - Success")
    void testGetOrderByIdSuccess() {
        when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(order));
        assertSame(currentInstance.findOrderById(1L).get(), order, "Orders should be the same");
        assertTrue(currentInstance.findOrderById(1L).isPresent(), "Order was  found");
        assertFalse(currentInstance.findOrderById(1L).isEmpty());
        assertNotEquals(234L, currentInstance.findOrderById(1L)
                .get()
                .getOrderId());
        verify(orderRepositoryMock, times(4)).findById(1L);
    }

    @Test
    @DisplayName(" order by id - Not Found")
    void testShouldThrowExceptionsWhenTheGivenIdIsNotFound() {
        when(orderRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        //assert
        assertThrows(ObjectWithIdNotFound.class, () -> currentInstance.findOrderById(1L));
        verify(orderRepositoryMock).findById(1L);
    }

    @Test
    @DisplayName("create a new Order - Success")
    void testCreateNewOrderSuccess() {
        Order mockOrder = new Order(123L, LocalDateTime.now().plusDays(1), customer, new HashSet<>());
        when(orderRepositoryMock.save(any())).thenReturn(mockOrder);
        var actualCurrent = currentInstance.saveOrder(mockOrder);
        //asserts
        assertEquals(mockOrder, actualCurrent);
        assertEquals(123L, actualCurrent.getOrderId().intValue());
        verify(orderRepositoryMock, atLeastOnce()).save(mockOrder);
    }

    @Test
    @DisplayName("Create Order - Not succeed")
    void testCreateOrderThrowExceptionWhenIsOrderIsNull() {
        when(orderRepositoryMock.save(null)).thenThrow(IllegalRequestException.class);
        assertThrows(IllegalRequestException.class, () -> currentInstance.saveOrder(null));
    }

    @Test
    @DisplayName("Update Order - Success")
    void testUpdateOrderSuccess() {
        Order existingOrder = new Order(1L, LocalDateTime.now(), customer, new HashSet<>());
        Order updatedOrder = new Order(1L, LocalDateTime.now().plusDays(1), customer, new HashSet<>());

        when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(existingOrder));

        when(orderRepositoryMock.save(existingOrder)).thenReturn(updatedOrder);

        Order result = currentInstance.updateOrder(1L, updatedOrder);

        // Verify that the expected order was returned
        assertEquals(updatedOrder, result);
    }

    @Test
    @DisplayName("Update Order - Order Not Found")
    void testUpdateOrderNotFound() {
        Order updatedOrder = new Order(1L, LocalDateTime.now(), customer, new HashSet<>());

        when(orderRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectWithIdNotFound.class, () -> currentInstance.updateOrder(1L, updatedOrder));
    }

    @Test
    @DisplayName("Update Order - Null Input Order")
    void testUpdateOrderNullInput() {
        assertThrows(IllegalRequestException.class, () -> currentInstance.updateOrder(1L, null));
    }

    @Test
    @DisplayName("Update Order - Null Values in Input")
    void testUpdateOrderNullValuesInInput() {
        Order updatedOrder = new Order(1L, LocalDateTime.now(), customer, new HashSet<>());
        when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(new Order()));
        assertThrows(NullPointerException.class, () -> currentInstance.updateOrder(1L, updatedOrder));
    }

    @Test
    @DisplayName("Delete Order - Success")
    void testDeleteOrderWithSuccess() {
        when(orderRepositoryMock.findById(1L))
                .thenReturn(Optional.of(order));
        var current = currentInstance.deleteOrderById(1);
        assertTrue(current);
        verify(orderRepositoryMock, times(1))
                .delete(order);
    }

    @Test
    @DisplayName(" Delete Order - Not Found")
    void testDeleteOrderNotFound() {
        when(orderRepositoryMock.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ObjectWithIdNotFound.class,
                () -> currentInstance.deleteOrderById(1L));
        verify(orderRepositoryMock).findById(1L);
    }

}