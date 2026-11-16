package com.consentflow.api.consent;

import com.consentflow.api.domain.Consent;
import com.consentflow.api.domain.enums.ConsentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, UUID> {
    // Find all consents for a specific user, with pagination
    Page<Consent> findByUserId(String userId, Pageable pageable);
    
    // Find all consents for a user filtered by status, with pagination
    Page<Consent> findByUserIdAndStatus(String userId, ConsentStatus status, Pageable pageable);
    
    // Find a specific consent by ID and User ID (enforces ownership)
    Optional<Consent> findByIdAndUserId(UUID id, String userId);
}