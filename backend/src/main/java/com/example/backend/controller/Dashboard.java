package com.example.backend.controller;

import com.example.backend.model.DashboardResponse;
import com.example.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class Dashboard {
    private final DashboardService dashboardService;

    public Dashboard(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard(@RequestParam("userId") Long userId) { ////GET /api/account/dashboard?user_id=123
        return ResponseEntity.ok(dashboardService.viewTransactionDashboard(userId));
    }
}