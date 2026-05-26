CREATE TABLE IF NOT EXISTS organization.organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    legal_name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_organizations_slug
    ON organization.organizations (slug);

CREATE TABLE IF NOT EXISTS identity.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    status VARCHAR(40) NOT NULL,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMPTZ,
    last_login_at TIMESTAMPTZ,
    password_changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    password_reset_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email_lower
    ON identity.users (LOWER(email));

CREATE INDEX IF NOT EXISTS idx_users_status
    ON identity.users (status);

CREATE TABLE IF NOT EXISTS identity.permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(120) NOT NULL,
    description VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_permissions_code
    ON identity.permissions (code);

CREATE TABLE IF NOT EXISTS identity.roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID REFERENCES organization.organizations(id),
    code VARCHAR(120) NOT NULL,
    name VARCHAR(150) NOT NULL,
    system_role BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_roles_organization_code
    ON identity.roles (organization_id, code);

CREATE INDEX IF NOT EXISTS idx_roles_organization_id
    ON identity.roles (organization_id);

CREATE TABLE IF NOT EXISTS identity.role_permissions (
    role_id UUID NOT NULL REFERENCES identity.roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES identity.permissions(id) ON DELETE RESTRICT,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS identity.organization_memberships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES identity.roles(id) ON DELETE RESTRICT,
    status VARCHAR(40) NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_memberships_user_organization
    ON identity.organization_memberships (user_id, organization_id);

CREATE INDEX IF NOT EXISTS idx_memberships_organization_status
    ON identity.organization_memberships (organization_id, status);

CREATE INDEX IF NOT EXISTS idx_memberships_user_status
    ON identity.organization_memberships (user_id, status);

CREATE TABLE IF NOT EXISTS identity.refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_id VARCHAR(100) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    user_id UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organization.organizations(id) ON DELETE CASCADE,
    status VARCHAR(40) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    replaced_by_token_id VARCHAR(100),
    issued_ip VARCHAR(100),
    issued_user_agent TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_refresh_tokens_token_id
    ON identity.refresh_tokens (token_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_refresh_tokens_token_hash
    ON identity.refresh_tokens (token_hash);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_status
    ON identity.refresh_tokens (user_id, status);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at
    ON identity.refresh_tokens (expires_at);

INSERT INTO identity.permissions (code, description)
VALUES
    ('RECONCILIATION_READ', 'Read reconciliation jobs, matches, and exceptions'),
    ('RECONCILIATION_EXECUTE', 'Execute reconciliation jobs and matching operations'),
    ('APPROVAL_APPROVE', 'Approve financial workflow tasks'),
    ('AUDIT_READ', 'Read immutable audit logs and compliance evidence'),
    ('USER_ADMIN', 'Administer users, roles, and organization membership')
ON CONFLICT (code) DO NOTHING;
