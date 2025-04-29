package com.myCompany.myWalletApp.entity;

import com.myCompany.myWalletApp.enums.OppositePartyType;
import com.myCompany.myWalletApp.enums.TransactionStatus;
import com.myCompany.myWalletApp.enums.TransactionType;
import jakarta.persistence.*;

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
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // DEPOSIT or WITHDRAW

    @Enumerated(EnumType.STRING)
    private OppositePartyType oppositePartyType; // IBAN or PAYMENT

    private String oppositeParty; // IBAN numarası veya ödeme ID'si

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // PENDING, APPROVED, DENIED

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private LocalDateTime createdAt = LocalDateTime.now();
}
