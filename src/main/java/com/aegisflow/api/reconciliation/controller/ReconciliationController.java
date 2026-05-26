package com.aegisflow.api.reconciliation.controller;

import com.aegisflow.api.common.api.ApiResponse;
import com.aegisflow.api.common.api.PageResponse;
import com.aegisflow.api.common.security.RequireReconciliationExceptionReview;
import com.aegisflow.api.common.security.RequireReconciliationRead;
import com.aegisflow.api.common.security.RequireReconciliationRun;
import com.aegisflow.api.common.security.SecurityUtils;
import com.aegisflow.api.reconciliation.dto.request.CreateReconciliationRuleRequest;
import com.aegisflow.api.reconciliation.dto.request.RunReconciliationRequest;
import com.aegisflow.api.reconciliation.dto.response.*;
import com.aegisflow.api.reconciliation.mapper.ReconciliationMapper;
import com.aegisflow.api.reconciliation.service.ReconciliationExecutionService;
import com.aegisflow.api.reconciliation.service.ReconciliationRuleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reconciliation")
public class ReconciliationController {
    private final ReconciliationRuleService ruleService;
    private final ReconciliationExecutionService executionService;
    private final ReconciliationMapper mapper;

    @RequireReconciliationRun
    @PostMapping("/rules")
    ApiResponse<ReconciliationRuleResponse> createRule(@Valid @RequestBody CreateReconciliationRuleRequest request) {
        return ApiResponse.success("Reconciliation rule created", mapper.toRuleResponse(ruleService.create(SecurityUtils.currentOrganizationId(), request)));
    }

    @RequireReconciliationRead
    @GetMapping("/rules")
    ApiResponse<List<ReconciliationRuleResponse>> listRules() {
        return ApiResponse.success(ruleService.list(SecurityUtils.currentOrganizationId()).stream().map(mapper::toRuleResponse).toList());
    }

    @RequireReconciliationRun
    @PostMapping("/jobs/run")
    ApiResponse<ReconciliationJobResponse> run(@Valid @RequestBody RunReconciliationRequest request) {
        return ApiResponse.success("Reconciliation job completed", executionService.run(SecurityUtils.currentOrganizationId(), request));
    }

    @RequireReconciliationRead
    @GetMapping("/jobs")
    ApiResponse<PageResponse<ReconciliationJobResponse>> jobs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size) {
        return ApiResponse.success(executionService.searchJobs(SecurityUtils.currentOrganizationId(), page, size));
    }

    @RequireReconciliationRead
    @GetMapping("/jobs/{jobId}")
    ApiResponse<ReconciliationJobResponse> getJob(@PathVariable UUID jobId) {
        return ApiResponse.success(mapper.toJobResponse(executionService.getJob(SecurityUtils.currentOrganizationId(), jobId)));
    }

    @RequireReconciliationRead
    @GetMapping("/jobs/{jobId}/matches")
    ApiResponse<PageResponse<ReconciliationMatchResponse>> matches(@PathVariable UUID jobId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size) {
        return ApiResponse.success(executionService.matches(SecurityUtils.currentOrganizationId(), jobId, page, size));
    }

    @RequireReconciliationExceptionReview
    @GetMapping("/jobs/{jobId}/exceptions")
    ApiResponse<PageResponse<ReconciliationExceptionResponse>> exceptions(@PathVariable UUID jobId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size) {
        return ApiResponse.success(executionService.exceptions(SecurityUtils.currentOrganizationId(), jobId, page, size));
    }

    @RequireReconciliationRead
    @GetMapping("/jobs/{jobId}/statistics")
    ApiResponse<ReconciliationStatisticsResponse> statistics(@PathVariable UUID jobId) {
        return ApiResponse.success(executionService.statistics(SecurityUtils.currentOrganizationId(), jobId));
    }
}
