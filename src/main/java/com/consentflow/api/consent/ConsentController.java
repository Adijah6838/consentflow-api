package com.consentflow.api.consent;

import com.consentflow.api.consent.dto.ConsentResponse;
import com.consentflow.api.consent.dto.CreateConsentRequest;
import com.consentflow.api.domain.ConsentHistory;
import com.consentflow.api.domain.enums.ConsentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consents")
@RequiredArgsConstructor
@Tag(name = "Consent Management", description = "Endpoints for managing document sharing consents")
public class ConsentController {

    private final ConsentService consentService;

    @PostMapping
    @Operation(summary = "Create a new consent record (Feature 1)")
    public ResponseEntity<ConsentResponse> createConsent(
            @Valid @RequestBody CreateConsentRequest request,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId) {
        
        request.setUserId(userId);
        ConsentResponse response = consentService.createConsent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a consent record (Feature 2)")
    public ResponseEntity<ConsentResponse> getConsent(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId) {
        
        return ResponseEntity.ok(consentService.getConsent(id, userId)); // 200
    }

    @GetMapping
    @Operation(summary = "List consents for a user with pagination and filtering (Features 5 & 6)")
    public ResponseEntity<Page<ConsentResponse>> listConsents(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) ConsentStatus status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        
        return ResponseEntity.ok(consentService.listConsents(userId, status, pageable));
    }

    @PatchMapping("/{id}/authorise")
    @Operation(summary = "Update authorization status to AUTHORISED (Feature 3)")
    public ResponseEntity<ConsentResponse> authoriseConsent(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId) {
        
        return ResponseEntity.ok(consentService.authoriseConsent(id, userId));
    }

    @PatchMapping("/{id}/revoke")
    @Operation(summary = "Revoke a consent (Feature 4)")
    public ResponseEntity<ConsentResponse> revokeConsent(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId) {
        
        return ResponseEntity.ok(consentService.revokeConsent(id, userId));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "View the audit history of a consent record (Feature 8)")
    public ResponseEntity<List<ConsentHistory>> getConsentHistory(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId) {
        
        // Verify ownership before showing history
        consentService.getConsent(id, userId);
        
        return ResponseEntity.ok(consentService.getConsentHistory(id));
    }
}
