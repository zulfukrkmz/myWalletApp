package com.myCompany.myWalletApp.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String type;
    private String status;
    private String oppositePartyType;
    private String oppositeParty;
    private LocalDateTime createdAt;
}