package com.aegisflow.api.identity.repository;

import com.aegisflow.api.identity.domain.MembershipStatus;
import com.aegisflow.api.identity.domain.OrganizationMembership;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, UUID> {

    @EntityGraph(attributePaths = {"organization", "role", "role.permissions"})
    List<OrganizationMembership> findByUserIdAndStatus(UUID userId, MembershipStatus status);

    @EntityGraph(attributePaths = {"organization", "role", "role.permissions", "user"})
    Optional<OrganizationMembership> findByUserIdAndOrganizationIdAndStatus(UUID userId, UUID organizationId, MembershipStatus status);

    boolean existsByUserIdAndOrganizationId(UUID userId, UUID organizationId);
}
