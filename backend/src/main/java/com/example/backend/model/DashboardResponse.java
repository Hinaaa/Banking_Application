package com.example.backend.model;

import java.util.List;

public record DashboardResponse(
        Long user_id,
        AccountDetailDashboardInfo accountDetailDashboardInfo,
        List<TransactionDto> transactionDashboard
) {
}
