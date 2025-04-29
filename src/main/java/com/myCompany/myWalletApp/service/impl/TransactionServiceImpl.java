package com.myCompany.myWalletApp.service.impl;

import com.myCompany.myWalletApp.dto.request.ApproveTransactionRequest;
import com.myCompany.myWalletApp.dto.response.TransactionResponse;
import com.myCompany.myWalletApp.entity.Transaction;
import com.myCompany.myWalletApp.entity.Wallet;
import com.myCompany.myWalletApp.enums.TransactionStatus;
import com.myCompany.myWalletApp.enums.TransactionType;
import com.myCompany.myWalletApp.repository.TransactionRepository;
import com.myCompany.myWalletApp.repository.WalletRepository;
import com.myCompany.myWalletApp.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public TransactionResponse approveOrDenyTransaction(Long transactionId, ApproveTransactionRequest request) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

            if (transaction.getStatus() != TransactionStatus.PENDING) {
                throw new IllegalStateException("Only pending transactions can be approved or denied");
            }

            TransactionStatus newStatus = TransactionStatus.valueOf(request.getStatus());

            transaction.setStatus(newStatus);

            Wallet wallet = transaction.getWallet();
            BigDecimal amount = transaction.getAmount();

            if (transaction.getType() == TransactionType.DEPOSIT) {
                if (newStatus == TransactionStatus.APPROVED) {
                    wallet.setUsableBalance(wallet.getUsableBalance().add(amount));
                } else if (newStatus == TransactionStatus.DENIED) {
                    wallet.setBalance(wallet.getBalance().subtract(amount));
                }
            } else if (transaction.getType() == TransactionType.WITHDRAW) {
                if (newStatus == TransactionStatus.APPROVED) {
                    wallet.setBalance(wallet.getBalance().subtract(amount));
                } else if (newStatus == TransactionStatus.DENIED) {
                    wallet.setUsableBalance(wallet.getUsableBalance().add(amount));
                }
            }

            walletRepository.save(wallet);
            transactionRepository.save(transaction);

            return TransactionResponse.builder()
                    .id(transaction.getId())
                    .amount(transaction.getAmount())
                    .type(transaction.getType().name())
                    .oppositePartyType(transaction.getOppositePartyType().name())
                    .oppositeParty(transaction.getOppositeParty())
                    .status(transaction.getStatus().name())
                    .build();
        } catch (EntityNotFoundException | IllegalStateException e) {
            logger.error("Error processing transaction with ID {}: {}", transactionId, e.getMessage(), e);
            throw e; // Hata durumunda yukarıya fırlatıyoruz
        } catch (Exception e) {
            logger.error("Unexpected error while processing transaction with ID {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while processing the transaction", e);
        }
    }
}