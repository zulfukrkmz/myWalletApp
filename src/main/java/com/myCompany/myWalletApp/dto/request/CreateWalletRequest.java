package com.myCompany.myWalletApp.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateWalletRequest {

    @NotBlank
    private Long customerId;

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