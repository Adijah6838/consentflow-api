package com.consentflow.api.domain;

import com.consentflow.api.domain.enums.ConsentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consent_history")
@Getter
@Setter
@NoArgsConstructor
public class ConsentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "consent_id", nullable = false)
    private Consent consent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus newStatus;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    public ConsentHistory(Consent consent, ConsentStatus previousStatus, ConsentStatus newStatus) {
        this.consent = consent;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = LocalDateTime.now();
    }
}