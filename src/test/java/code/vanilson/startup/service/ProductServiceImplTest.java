package code.vanilson.startup.service;

import code.vanilson.startup.dto.ProductDto;
import code.vanilson.startup.exception.ObjectWithIdNotFound;
import code.vanilson.startup.model.Product;
import code.vanilson.startup.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    /**
     * code.vanilson.startup.product.ProductSteps Repository Mock
     */
    ProductRepository productRepositoryMock;
    /**
     * current instance
     */
    ProductServiceImpl currentInstance;
    /**
     * code.vanilson.startup.product.ProductSteps instance
     */
    Product product;
    /**
     * Lists of Products
     */
    List<Product> products;

    /**
     * setup
     */
    @BeforeEach
    void setUp() {
        productRepositoryMock = mock(ProductRepository.class);
        currentInstance = new ProductServiceImpl(productRepositoryMock);

        product = new Product(1, "Keyboard", 34, 1);
        products = List.of(
                new Product(1, "Computer", 34, 2004),
                new Product(2, "Mouse", 10, 1)
        );
    }

    @Test
    @DisplayName("List all products - Success")
    void testShouldReturnAllProducts() {
        // Mock repository behavior
        when(productRepositoryMock.findAll()).thenReturn(products);

        // Call the method under test
        var productDtos = currentInstance.findAll();

        // Assertions
        assertNotNull(productDtos, "true");
        assertEquals(2, productDtos.size(), "Should return 2");
        assertEquals(1, productDtos.get(0).getProductId().intValue());
        assertEquals(2, productDtos.get(1).getProductId().intValue());
        assertEquals("Computer", productDtos.get(0).getName());

        // Verify that the repository method was called
        verify(productRepositoryMock, times(1)).findAll();

    }

    @Test
    @DisplayName("Return product by id - Found")
    void testShouldReturnTheProductsWhenTheGivenIdIsFound() {
        when(productRepositoryMock.findById(1)).thenReturn(Optional.of(product));
        var productDtos = currentInstance.findById(1).get();
        assertSame(productDtos.getProductId(), product.getProductId(), "Products should be the same");
        assertTrue(currentInstance.findById(1).isPresent(), "true");
        assertFalse(currentInstance.findById(1).isEmpty());
        assertNotEquals(234, currentInstance.findById(1)
                .get()
                .getProductId());
        verify(productRepositoryMock, times(4)).findById(1);
    }

    @Test
    @DisplayName(" product by id - Not Found")
    void testShouldThrowExceptionsWhenTheGivenIdIsNotFound() {
        when(productRepositoryMock.findById(1)).thenReturn(Optional.empty());
        //assert
        assertThrows(ObjectWithIdNotFound.class, () -> currentInstance.findById(1));
    }

    @Test
    @DisplayName("create a new product - Success")
    void testShouldCreateNewProduct() {
        ProductDto productDtoToCreate = new ProductDto(1, "keyboard", 10, null);
        Product mockProduct = new Product(1, "keyboard", 10, 1);
        when(productRepositoryMock.save(any(Product.class))).thenReturn(mockProduct);
        ProductDto createdProductDto = currentInstance.save(productDtoToCreate);

        // Asserts
        assertEquals(1, createdProductDto.getProductId().intValue());
        assertEquals("keyboard", createdProductDto.getName());
        assertEquals(10, createdProductDto.getQuantity().intValue());
        //verify
        verify(productRepositoryMock, atLeastOnce()).save(any(Product.class));
    }

    @Test
    @DisplayName("update product - Success")
    void testShouldUpdateProduct() {
        ProductDto updatedProductDto = new ProductDto(1, "new_keyboard", 20, 1);
        when(productRepositoryMock.update(any(Product.class))).thenReturn(true);
        boolean updateResult = currentInstance.update(updatedProductDto);
        assertTrue(updateResult, "Update operation should return true");
        assertEquals(1, updatedProductDto.getProductId().intValue(), "Product ID should be 1 after update");
        assertEquals("new_keyboard", updatedProductDto.getName(), "Product name should be 'new_keyboard' after update");
        assertEquals(20, updatedProductDto.getQuantity().intValue(), "Product quantity should be 20 after update");
        assertEquals(1, updatedProductDto.getVersion().intValue(), "Product version should be 1 after update");
        verify(productRepositoryMock, atLeastOnce()).update(any(Product.class));
    }

    @Test
    @DisplayName("delete product - Success")
    void testShouldDeleteProduct() {
        Integer existingProductId = 1;
        when(productRepositoryMock.findById(existingProductId)).thenReturn(
                Optional.of(new Product(existingProductId, "keyboard", 10, 1)));
        when(productRepositoryMock.delete(existingProductId)).thenReturn(true);
        boolean deleteResult = currentInstance.delete(existingProductId);
        assertTrue(deleteResult);
        verify(productRepositoryMock, times(1)).findById(existingProductId);
        verify(productRepositoryMock, times(1)).delete(existingProductId);
    }

    @Test
    @DisplayName("Delete product by id - Not Success")
    void testDeleteProductsThrowAnException() {
        Integer nonExistingProductId = 2;
        when(productRepositoryMock.findById(nonExistingProductId)).thenReturn(Optional.empty());
        ObjectWithIdNotFound exception =
                assertThrows(ObjectWithIdNotFound.class, () -> currentInstance.delete(nonExistingProductId));

        // Asserts
        assertEquals(" product with 2 not found", exception.getMessage());

        // Verify that findById was called as expected
        verify(productRepositoryMock, times(1)).findById(nonExistingProductId);
    }
}