CREATE TABLE consents (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    document_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    authorised_at TIMESTAMP,
    revoked_at TIMESTAMP
);

CREATE TABLE consent_history (
    id UUID PRIMARY KEY,
    consent_id UUID NOT NULL REFERENCES consents(id),
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP NOT NULL
);
