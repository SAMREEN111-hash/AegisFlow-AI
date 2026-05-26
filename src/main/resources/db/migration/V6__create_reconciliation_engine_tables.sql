INSERT INTO identity.permissions (code, description)
VALUES
    ('RECONCILIATION_RUN', 'Run reconciliation jobs'),
    ('RECONCILIATION_OVERRIDE', 'Manually override reconciliation outcomes'),
    ('RECONCILIATION_EXCEPTION_REVIEW', 'Review and resolve reconciliation exceptions')
ON CONFLICT (code) DO NOTHING;

CREATE TABLE IF NOT EXISTS reconciliation.reconciliation_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    name VARCHAR(180) NOT NULL,
    primary_record_type VARCHAR(50) NOT NULL,
    candidate_record_type VARCHAR(50) NOT NULL,
    amount_tolerance NUMERIC(19,4) NOT NULL DEFAULT 0,
    timestamp_tolerance_hours INTEGER NOT NULL DEFAULT 24,
    reference_similarity_threshold NUMERIC(5,4) NOT NULL DEFAULT 0.8500,
    auto_match_confidence_threshold NUMERIC(5,4) NOT NULL DEFAULT 0.9000,
    require_currency_match BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reconciliation_rules_org_active
    ON reconciliation.reconciliation_rules (organization_id, active);

CREATE TABLE IF NOT EXISTS reconciliation.reconciliation_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    rule_id UUID NOT NULL REFERENCES reconciliation.reconciliation_rules(id) ON DELETE RESTRICT,
    status VARCHAR(50) NOT NULL,
    name VARCHAR(180) NOT NULL,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    candidate_count INTEGER NOT NULL DEFAULT 0,
    matched_count INTEGER NOT NULL DEFAULT 0,
    exception_count INTEGER NOT NULL DEFAULT 0,
    failure_reason VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reconciliation_jobs_org_status_created
    ON reconciliation.reconciliation_jobs (organization_id, status, created_at DESC);

CREATE TABLE IF NOT EXISTS reconciliation.reconciliation_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    job_id UUID NOT NULL REFERENCES reconciliation.reconciliation_jobs(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    execution_summary JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS reconciliation.reconciliation_matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    job_id UUID NOT NULL REFERENCES reconciliation.reconciliation_jobs(id) ON DELETE CASCADE,
    primary_transaction_id UUID NOT NULL REFERENCES finance.financial_transactions(id) ON DELETE RESTRICT,
    candidate_transaction_id UUID NOT NULL REFERENCES finance.financial_transactions(id) ON DELETE RESTRICT,
    match_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    confidence_score NUMERIC(5,4) NOT NULL,
    amount_variance NUMERIC(19,4) NOT NULL,
    matched_by_strategy VARCHAR(120) NOT NULL,
    explanation JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reconciliation_matches_job
    ON reconciliation.reconciliation_matches (job_id, confidence_score DESC);

CREATE TABLE IF NOT EXISTS reconciliation.reconciliation_exceptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    job_id UUID NOT NULL REFERENCES reconciliation.reconciliation_jobs(id) ON DELETE CASCADE,
    transaction_id UUID NOT NULL REFERENCES finance.financial_transactions(id) ON DELETE RESTRICT,
    exception_type VARCHAR(80) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reconciliation_exceptions_org_status
    ON reconciliation.reconciliation_exceptions (organization_id, status, created_at DESC);

CREATE TABLE IF NOT EXISTS reconciliation.reconciliation_decision_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    job_id UUID NOT NULL REFERENCES reconciliation.reconciliation_jobs(id) ON DELETE CASCADE,
    decision_type VARCHAR(100) NOT NULL,
    decision_payload JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reconciliation_decision_audit_job
    ON reconciliation.reconciliation_decision_audit (job_id, created_at DESC);
