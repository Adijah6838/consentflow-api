package com.consentflow.api;

import com.consentflow.api.consent.dto.CreateConsentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:15435/consentflow",
    "spring.datasource.username=postgres",
    "spring.datasource.password=postgres"
})
public class ConsentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFullConsentLifecycleAndInvalidTransition() throws Exception {
        CreateConsentRequest request = new CreateConsentRequest();
        request.setDocumentId("doc_123");
        request.setUserId("user_1");

        // 1. Create Consent
        MvcResult result = mockMvc.perform(post("/api/v1/consents")
                .header("X-User-Id", "user_1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AWAITING_AUTHORISATION"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        String consentId = objectMapper.readTree(responseString).get("id").asText();

        // 2. Authorise Consent
        mockMvc.perform(patch("/api/v1/consents/" + consentId + "/authorise")
                .header("X-User-Id", "user_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AUTHORISED"));

        // 3. Revoke Consent
        mockMvc.perform(patch("/api/v1/consents/" + consentId + "/revoke")
                .header("X-User-Id", "user_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REVOKED"));

        // 4. Try to Authorise again (Invalid Transition)
        mockMvc.perform(patch("/api/v1/consents/" + consentId + "/authorise")
                .header("X-User-Id", "user_1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Invalid transition: Cannot authorise a consent that is REVOKED"));

        // 5. Try to access as different user (Ownership check)
        mockMvc.perform(get("/api/v1/consents/" + consentId)
                .header("X-User-Id", "hacker"))
                .andExpect(status().isNotFound());
    }
}
