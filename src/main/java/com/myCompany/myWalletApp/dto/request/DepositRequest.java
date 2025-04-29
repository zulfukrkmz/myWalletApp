package com.myCompany.myWalletApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class DepositRequest {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long walletId;

    @NotBlank
    private String source;

    @NotNull
    @Pattern(regexp = "^(IBAN|PAYMENT)$")
    private String sourceType;
}