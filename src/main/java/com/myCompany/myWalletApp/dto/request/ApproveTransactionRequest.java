package com.myCompany.myWalletApp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveTransactionRequest {

    @NotNull
    private Long transactionId;

    @NotNull
    @Pattern(regexp = "^(APPROVED|DENIED)$")
    private String status;
}