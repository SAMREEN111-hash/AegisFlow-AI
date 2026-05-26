CREATE TABLE IF NOT EXISTS audit.audit_event_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID,
    actor_id UUID,
    actor_type VARCHAR(50) NOT NULL,
    action VARCHAR(150) NOT NULL,
    entity_type VARCHAR(150) NOT NULL,
    entity_id UUID,
    request_id VARCHAR(100),
    correlation_id VARCHAR(100),
    ip_address VARCHAR(100),
    user_agent TEXT,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_event_log_organization_created_at
    ON audit.audit_event_log (organization_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_audit_event_log_entity
    ON audit.audit_event_log (entity_type, entity_id);
