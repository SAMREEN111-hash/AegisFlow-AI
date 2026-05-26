package com.aegisflow.api.identity.service;

import com.aegisflow.api.organizations.repository.OrganizationRepository;
import java.text.Normalizer;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationSlugService {

    private final OrganizationRepository organizationRepository;

    public String createUniqueSlug(String organizationName) {
        String baseSlug = Normalizer.normalize(organizationName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (baseSlug.isBlank()) {
            baseSlug = "organization";
        }

        String candidate = baseSlug;
        int suffix = 2;
        while (organizationRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + suffix;
            suffix++;
        }
        return candidate;
    }
}
