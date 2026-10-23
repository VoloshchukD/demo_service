package com.dev.demo_service.controller;

import com.dev.demo_service.entity.Product;
import com.dev.demo_service.security.SecurityConfig;
import com.dev.demo_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void listsProductsForAuthenticatedUser() throws Exception {
        when(productService.findAll()).thenReturn(List.of(
                new Product("Keyboard", "Mechanical keyboard", new BigDecimal("89.99"), 5)));

        mockMvc.perform(get("/api/products").with(jwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Keyboard"))
                .andExpect(jsonPath("$[0].description").value("Mechanical keyboard"))
                .andExpect(jsonPath("$[0].price").value(89.99))
                .andExpect(jsonPath("$[0].quantity").value(5));
    }

    @Test
    void returnsProductByIdForAuthenticatedUser() throws Exception {
        when(productService.findById(7L)).thenReturn(
                new Product("Keyboard", "Mechanical keyboard", new BigDecimal("89.99"), 5));

        mockMvc.perform(get("/api/products/7").with(jwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Keyboard"));
    }

    @Test
    void createsProductForAdmin() throws Exception {
        when(productService.create(any(Product.class))).thenReturn(
                new Product("Keyboard", "Mechanical keyboard", new BigDecimal("89.99"), 5));

        mockMvc.perform(post("/api/products")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"Keyboard","description":"Mechanical keyboard","price":89.99,"quantity":5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void updatesProductForAdmin() throws Exception {
        when(productService.update(eq(7L), any(Product.class))).thenReturn(
                new Product("Updated keyboard", "Updated description", new BigDecimal("99.99"), 4));

        mockMvc.perform(put("/api/products/7")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"Updated keyboard","description":"Updated description","price":99.99,"quantity":4}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated keyboard"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void deletesProductForAdmin() throws Exception {
        mockMvc.perform(delete("/api/products/7").with(jwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(productService).delete(7L);
    }

    @Test
    void rejectsWritesFromUserRole() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"Keyboard","description":"Mechanical keyboard","price":89.99,"quantity":5}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsProductReadsAndWritesForJwtWithoutRoles() throws Exception {
        var tokenWithoutRoles = jwt().jwt(jwt -> jwt
                .claim("realm_access", Map.of("roles", List.of())));

        mockMvc.perform(get("/api/products").with(tokenWithoutRoles))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/products")
                        .with(tokenWithoutRoles)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"Keyboard","description":"Mechanical keyboard","price":89.99,"quantity":5}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsUnauthenticatedProductReadsAndWrites() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"Keyboard","description":"Mechanical keyboard","price":89.99,"quantity":5}
                                """))
                .andExpect(status().isUnauthorized());
    }

}