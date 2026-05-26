package com.aegisflow.api.identity.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles", schema = "identity")
public class Role extends BaseAuditableEntity {

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "code", nullable = false, length = 120)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "system_role", nullable = false)
    private boolean systemRole;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            schema = "identity",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
}
