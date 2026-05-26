package com.aegisflow.api.identity.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions", schema = "identity")
public class Permission extends BaseAuditableEntity {

    @Column(name = "code", nullable = false, unique = true, length = 120)
    private String code;

    @Column(name = "description", nullable = false, length = 500)
    private String description;
}
