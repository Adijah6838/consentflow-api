package com.consentflow.api.domain;

import com.consentflow.api.domain.enums.ConsentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consents")
@Getter
@Setter
@NoArgsConstructor
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId; // The owner of the document

    @Column(nullable = false)
    private String documentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime authorisedAt;
    private LocalDateTime revokedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ConsentStatus.AWAITING_AUTHORISATION;
        }
    }
}