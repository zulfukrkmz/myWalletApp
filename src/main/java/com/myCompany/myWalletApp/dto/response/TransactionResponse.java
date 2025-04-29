package com.myCompany.myWalletApp.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String type;
    private String status;
    private String oppositePartyType;
    private String oppositeParty;
    private LocalDateTime createdAt;
}