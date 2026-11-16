package com.consentflow.api.consent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateConsentRequest {
    @NotBlank(message = "Document ID is required")
    private String documentId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
}
