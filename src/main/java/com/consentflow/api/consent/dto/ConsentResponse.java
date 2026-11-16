package com.consentflow.api.consent.dto;

import com.consentflow.api.domain.enums.ConsentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConsentResponse {
    private UUID id;
    private String userId;
    private String documentId;
    private ConsentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime authorisedAt;
    private LocalDateTime revokedAt;
}
