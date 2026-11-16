package com.consentflow.api.consent;

import com.consentflow.api.domain.ConsentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConsentHistoryRepository extends JpaRepository<ConsentHistory, UUID> {
    List<ConsentHistory> findByConsentIdOrderByChangedAtDesc(UUID consentId);
}