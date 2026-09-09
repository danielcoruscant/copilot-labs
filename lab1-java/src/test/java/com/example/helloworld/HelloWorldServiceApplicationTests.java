package com.example.helloworld;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
class HelloWorldServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesHealthAndHelloEndpoints() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello world!"));
    }

    @Test
    void supportsCustomerCrudLifecycle() throws Exception {
        String response = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada Lovelace\",\"email\":\"ada@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andReturn().getResponse().getContentAsString();
        String customerId = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        mockMvc.perform(get("/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId));

        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada Byron\",\"email\":\"ada.byron@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ada Byron"));
        mockMvc.perform(delete("/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));
        mockMvc.perform(get("/customers/{id}", customerId))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/customers/{id}", customerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsValidationAndNotFoundErrors() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"x\"}"))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(get("/customers/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/customers/not-a-uuid"))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(put("/customers/00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada\",\"email\":\"ada@example.com\"}"))
                .andExpect(status().isNotFound());

        String created = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada\",\"email\":\"ada@example.com\"}"))
                .andReturn().getResponse().getContentAsString();
        String customerId = com.jayway.jsonpath.JsonPath.read(created, "$.id");
        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada\",\"email\":\"invalid\"}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
