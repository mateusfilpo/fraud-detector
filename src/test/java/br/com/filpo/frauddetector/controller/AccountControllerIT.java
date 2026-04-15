package br.com.filpo.frauddetector.controller;

import br.com.filpo.frauddetector.Neo4jIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerIT extends Neo4jIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAccount() throws Exception {
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"holder": "Test User", "type": "CHECKING"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").isNotEmpty())
                .andExpect(jsonPath("$.holder").value("Test User"))
                .andExpect(jsonPath("$.type").value("CHECKING"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturnAllAccounts() throws Exception {
        // Given: criar uma conta
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"holder": "List User", "type": "SAVINGS"}
                        """))
                .andExpect(status().isCreated());

        // When/Then
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].holder").value("List User"));
    }

    @Test
    void shouldReturn404ForNonExistentAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldSuspendAccount() throws Exception {
        // Given
        String response = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"holder": "Suspend User", "type": "CHECKING"}
                        """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String accountId = com.jayway.jsonpath.JsonPath.read(response, "$.accountId");

        // When/Then
        mockMvc.perform(patch("/api/accounts/" + accountId + "/suspend"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(jsonPath("$.status").value("SUSPENDED"));
    }
}
