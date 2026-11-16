package com.consentflow.api.consent;

import com.consentflow.api.consent.dto.ConsentResponse;
import com.consentflow.api.consent.dto.CreateConsentRequest;
import com.consentflow.api.domain.Consent;
import com.consentflow.api.domain.ConsentHistory;
import com.consentflow.api.domain.enums.ConsentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final ConsentHistoryRepository historyRepository;

    @Transactional
    public ConsentResponse createConsent(CreateConsentRequest request) {
        Consent consent = new Consent();
        consent.setUserId(request.getUserId());
        consent.setDocumentId(request.getDocumentId());
        consent.setStatus(ConsentStatus.AWAITING_AUTHORISATION);
        
        Consent savedConsent = consentRepository.save(consent);
        historyRepository.save(new ConsentHistory(savedConsent, null, ConsentStatus.AWAITING_AUTHORISATION));
        
        return mapToResponse(savedConsent);
    }

    @Transactional(readOnly = true)
    public ConsentResponse getConsent(UUID id, String userId) {
        Consent consent = consentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Consent not found or access denied"));
        return mapToResponse(consent);
    }

    @Transactional(readOnly = true)
    public Page<ConsentResponse> listConsents(String userId, ConsentStatus status, Pageable pageable) {
        Page<Consent> consents;
        if (status != null) {
            consents = consentRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            consents = consentRepository.findByUserId(userId, pageable);
        }
        return consents.map(this::mapToResponse);
    }

    @Transactional
    public ConsentResponse authoriseConsent(UUID id, String userId) {
        Consent consent = consentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Consent not found or access denied"));

        if (consent.getStatus() != ConsentStatus.AWAITING_AUTHORISATION) {
            throw new IllegalStateException("Invalid transition: Cannot authorise a consent that is " + consent.getStatus());
        }

        ConsentStatus previousStatus = consent.getStatus();
        consent.setStatus(ConsentStatus.AUTHORISED);
        consent.setAuthorisedAt(LocalDateTime.now());
        
        Consent updatedConsent = consentRepository.save(consent);
        historyRepository.save(new ConsentHistory(updatedConsent, previousStatus, ConsentStatus.AUTHORISED));
        
        return mapToResponse(updatedConsent);
    }

    @Transactional
    public ConsentResponse revokeConsent(UUID id, String userId) {
        Consent consent = consentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Consent not found or access denied"));

        if (consent.getStatus() == ConsentStatus.REVOKED || consent.getStatus() == ConsentStatus.EXPIRED) {
            throw new IllegalStateException("Invalid transition: Cannot revoke a consent that is already " + consent.getStatus());
        }

        ConsentStatus previousStatus = consent.getStatus();
        consent.setStatus(ConsentStatus.REVOKED);
        consent.setRevokedAt(LocalDateTime.now());
        
        Consent updatedConsent = consentRepository.save(consent);
        historyRepository.save(new ConsentHistory(updatedConsent, previousStatus, ConsentStatus.REVOKED));
        
        return mapToResponse(updatedConsent);
    }

    @Transactional(readOnly = true)
    public List<ConsentHistory> getConsentHistory(UUID consentId) {
        return historyRepository.findByConsentIdOrderByChangedAtDesc(consentId);
    }

    private ConsentResponse mapToResponse(Consent consent) {
        return ConsentResponse.builder()
                .id(consent.getId())
                .userId(consent.getUserId())
                .documentId(consent.getDocumentId())
                .status(consent.getStatus())
                .createdAt(consent.getCreatedAt())
                .authorisedAt(consent.getAuthorisedAt())
                .revokedAt(consent.getRevokedAt())
                .build();
    }
}
