package com.myCompany.myWalletApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class WithdrawRequest {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long walletId;

    @NotBlank
    private String destination;

    @NotNull
    @Pattern(regexp = "^(IBAN|PAYMENT)$")
    private String destinationType;
}