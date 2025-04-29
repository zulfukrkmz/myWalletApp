package com.myCompany.myWalletApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateWalletRequest {

    @NotBlank
    private String walletName;

    @NotNull
    @Pattern(regexp = "^(TRY|USD|EUR)$")
    private String currency;

    @NotNull
    private Boolean activeForShopping;

    @NotNull
    private Boolean activeForWithdraw;
}