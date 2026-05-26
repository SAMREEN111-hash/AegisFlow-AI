package com.aegisflow.api.identity.dto.response;

import java.util.List;
import java.util.UUID;

public record OrganizationMembershipResponse(
        UUID organizationId,
        String organizationName,
        UUID roleId,
        String roleCode,
        List<String> permissions
) {
}
