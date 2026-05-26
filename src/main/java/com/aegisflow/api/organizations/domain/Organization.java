package com.aegisflow.api.organizations.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "organizations", schema = "organization")
public class Organization extends BaseAuditableEntity {

    @Column(name = "legal_name", nullable = false, length = 255)
    private String legalName;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(name = "slug", nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
