INSERT INTO identity.permissions (code, description)
VALUES
    ('TRANSACTION_READ', 'Read normalized financial transaction records and ingestion status'),
    ('TRANSACTION_INGEST', 'Upload and ingest financial transaction files')
ON CONFLICT (code) DO NOTHING;

CREATE TABLE IF NOT EXISTS finance.transaction_sources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    record_type VARCHAR(50) NOT NULL,
    source_name VARCHAR(150) NOT NULL,
    provider_name VARCHAR(150),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_transaction_sources_org_type_name
    ON finance.transaction_sources (organization_id, record_type, source_name);

CREATE TABLE IF NOT EXISTS finance.ingestion_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    source_id UUID NOT NULL REFERENCES finance.transaction_sources(id) ON DELETE RESTRICT,
    status VARCHAR(50) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(150),
    file_size_bytes BIGINT NOT NULL,
    total_records INTEGER NOT NULL DEFAULT 0,
    processed_records INTEGER NOT NULL DEFAULT 0,
    failed_records INTEGER NOT NULL DEFAULT 0,
    duplicate_records INTEGER NOT NULL DEFAULT 0,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    failure_reason VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ingestion_jobs_org_status_created
    ON finance.ingestion_jobs (organization_id, status, created_at DESC);

CREATE TABLE IF NOT EXISTS finance.ingestion_batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    job_id UUID NOT NULL REFERENCES finance.ingestion_jobs(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    batch_sequence INTEGER NOT NULL,
    record_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_ingestion_batches_job_sequence
    ON finance.ingestion_batches (job_id, batch_sequence);

CREATE TABLE IF NOT EXISTS finance.financial_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    source_id UUID NOT NULL REFERENCES finance.transaction_sources(id) ON DELETE RESTRICT,
    ingestion_job_id UUID NOT NULL REFERENCES finance.ingestion_jobs(id) ON DELETE RESTRICT,
    ingestion_batch_id UUID NOT NULL REFERENCES finance.ingestion_batches(id) ON DELETE RESTRICT,
    record_type VARCHAR(50) NOT NULL,
    external_reference VARCHAR(255) NOT NULL,
    counterparty_name VARCHAR(255),
    description VARCHAR(1000),
    transaction_timestamp TIMESTAMPTZ NOT NULL,
    posting_timestamp TIMESTAMPTZ,
    currency_code CHAR(3) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    direction VARCHAR(20) NOT NULL,
    reconciliation_status VARCHAR(50) NOT NULL,
    duplicate_fingerprint VARCHAR(128) NOT NULL,
    raw_payload JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_financial_transactions_org_fingerprint
    ON finance.financial_transactions (organization_id, duplicate_fingerprint);

CREATE INDEX IF NOT EXISTS idx_financial_transactions_org_timestamp
    ON finance.financial_transactions (organization_id, transaction_timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_financial_transactions_org_reconciliation_status
    ON finance.financial_transactions (organization_id, reconciliation_status);

CREATE INDEX IF NOT EXISTS idx_financial_transactions_org_external_reference
    ON finance.financial_transactions (organization_id, external_reference);

CREATE TABLE IF NOT EXISTS finance.transaction_metadata (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    transaction_id UUID NOT NULL REFERENCES finance.financial_transactions(id) ON DELETE CASCADE,
    metadata_key VARCHAR(150) NOT NULL,
    metadata_value VARCHAR(2000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_transaction_metadata_transaction
    ON finance.transaction_metadata (transaction_id);

CREATE TABLE IF NOT EXISTS finance.ingestion_errors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    job_id UUID NOT NULL REFERENCES finance.ingestion_jobs(id) ON DELETE CASCADE,
    batch_id UUID REFERENCES finance.ingestion_batches(id) ON DELETE SET NULL,
    row_number BIGINT NOT NULL,
    error_code VARCHAR(100) NOT NULL,
    error_message VARCHAR(1000) NOT NULL,
    raw_payload JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ingestion_errors_job_row
    ON finance.ingestion_errors (job_id, row_number);
