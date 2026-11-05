package com.dev.demo_service.security;

import com.dev.demo_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void clearProducts() {
        productRepository.deleteAll();
    }

    @Test
    void supportsProductCrudEndToEnd() throws Exception {
        String productJson = """
                {"name":"Desk","description":"Standing desk","price":349.99,"quantity":6}
                """;

        mockMvc.perform(post("/api/products")
                        .with(adminJwt())
                        .contentType(APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Desk"))
                .andExpect(jsonPath("$.quantity").value(6));
        Long productId = productRepository.findAll().getFirst().getId();

        mockMvc.perform(get("/api/products").with(userJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productId))
                .andExpect(jsonPath("$[0].name").value("Desk"));

        mockMvc.perform(get("/api/products/{id}", productId).with(userJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Standing desk"));

        mockMvc.perform(put("/api/products/{id}", productId)
                        .with(adminJwt())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"Updated desk","description":"Electric standing desk","price":399.99,"quantity":4}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated desk"))
                .andExpect(jsonPath("$.price").value(399.99));

        mockMvc.perform(get("/api/products/{id}", productId).with(userJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(4));

        mockMvc.perform(delete("/api/products/{id}", productId).with(adminJwt()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products/{id}", productId).with(userJwt()))
                .andExpect(status().isNotFound());
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor adminJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor userJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

}