package com.aegisflow.api.identity.repository;

import com.aegisflow.api.identity.domain.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByOrganizationIdAndCode(UUID organizationId, String code);
}
