package code.vanilson.startup.controller;

import code.vanilson.startup.model.Product;
import code.vanilson.startup.service.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ProductServiceImpl productServiceMock;

    Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1, "Computer", 34, 2004);
    }


    @Test
    @DisplayName("GET /api/products -Success")
    void testGetProductSuccess() throws Exception {

        when(productServiceMock.findAll())
                .thenReturn(
                        List.of(
                                new Product(1, "Computer", 34, 2004),
                                new Product(2, "Mouse", 10, 1)
                        )
                );
        mockMvc.perform(get("/api/products"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Computer"))
                .andExpect(jsonPath("$[0].quantity").value(34))
                .andExpect(jsonPath("$[0].version").value(2004));

        verify(productServiceMock, times(1)).findAll();

    }

    @Test
    @DisplayName("GET /api/products/{id} -Found")
    void testGetProductByIdSuccess() throws Exception {

        when(productServiceMock.findById(1)).thenReturn(Optional.of(product));
        mockMvc.perform(get("/api/products/{id}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "\"2004\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/1"))
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Computer"))
                .andExpect(jsonPath("$.quantity").value(34))
                .andExpect(jsonPath("$.version").value(2004));

        verify(productServiceMock, times(1)).findById(1);

    }

    @Test
    @DisplayName("GET /api/products/{id} -Not Found")
    void testGetProductByIdNotFound() throws Exception {

        when(productServiceMock.findById(1)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/products/{id}", 1))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("POST /api/products/create - Success")
    void testPostCreateNewProduct() throws Exception {
        Product postProduct = new Product("keyboard", 10);
        Product mockProduct = new Product(1, "keyboard", 10, 1);


        when(productServiceMock.save(any())).thenReturn(mockProduct);
        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                //validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/1"))
                // return fields
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("keyboard"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.version").value(1));

        verify(productServiceMock, times(1)).save(any());

    }


    @Test
    @DisplayName("PUT /api/products/update/{id} - Success")
    void testPuttUpdateProductSuccess() throws Exception {
        Product putProduct = new Product("keyboard", 10);
        Product mockProduct = new Product(1, "keyboard", 10, 1);

        when(productServiceMock.findById(1)).thenReturn(Optional.of(mockProduct));
        when(productServiceMock.update(any())).thenReturn(true);

        mockMvc.perform(put("/api/products/update/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.IF_MATCH, 1)
                        .content(asJsonString(putProduct)))
                //validate the  response content type and code
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                //validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/1"))
                // return fields
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("keyboard"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.version").value(2));

        verify(productServiceMock, times(1)).update(mockProduct);

    }

    @Test
    @DisplayName("PUT /api/products/update/{id} -Not Found")
    void testPutProductNotFound() throws Exception {

        when(productServiceMock.findById(1)).thenReturn(Optional.empty());
        when(productServiceMock.update(any())).thenReturn(false);
        mockMvc.perform(get("/api/products/{id}", 1))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("PUT /api/products/update/{id} -Not Found")
    void testProductPutVersionMisMatch() throws Exception {

        Product putProduct = new Product("keyboard", 10);
        Product mockProduct = new Product(1, "keyboard", 10, 2);

        when(productServiceMock.findById(1)).thenReturn(Optional.of(mockProduct));
        when(productServiceMock.update(any())).thenReturn(false);

        mockMvc.perform(put("/api/products/update/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.IF_MATCH, 1)
                        .content(asJsonString(putProduct)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/products/delete/{id} -Found")
    void testDeleteProductByIdSuccess() throws Exception {

        when(productServiceMock.findById(1)).thenReturn(Optional.of(product));
        when(productServiceMock.delete(1)).thenReturn(true);
        mockMvc.perform(delete("/api/products/delete/{id}", 1))
                .andExpect(status().isOk());

        verify(productServiceMock, times(1)).delete(1);

    }

    @Test
    @DisplayName("DELETE /api/products/delete/{id} -Not Found")
    void testDeleteProductByIdNotFound() throws Exception {

        when(productServiceMock.findById(1)).thenReturn(Optional.empty());
        when(productServiceMock.delete(any())).thenReturn(false);
        mockMvc.perform(delete("/api/products/delete/{id}", 1))
                .andExpect(status().isNotFound());


    }

    @Test
    @DisplayName("DELETE /api/products/delete/{id} -Failure")
    void testDeleteProductFailure() throws Exception {
        when(productServiceMock.findById(1)).thenReturn(Optional.of(product));
        when(productServiceMock.delete(1)).thenReturn(false);
        mockMvc.perform(delete("/api/products/delete/{id}", 1))
                .andExpect(status().isInternalServerError());


    }


    /**
     * Method auxiliary to convert object in json
     *
     * @param obj obj
     * @return obj
     */
    protected String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}