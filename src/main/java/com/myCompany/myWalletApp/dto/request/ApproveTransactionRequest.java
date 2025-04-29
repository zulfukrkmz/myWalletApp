package com.myCompany.myWalletApp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ApproveTransactionRequest {

    @NotNull
    private Long transactionId;

    @NotNull
    @Pattern(regexp = "^(APPROVED|DENIED)$")
    private String status;
}