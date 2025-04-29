package com.myCompany.myWalletApp.service.impl;

import com.myCompany.myWalletApp.dto.request.CreateWalletRequest;
import com.myCompany.myWalletApp.dto.request.DepositRequest;
import com.myCompany.myWalletApp.dto.request.WithdrawRequest;
import com.myCompany.myWalletApp.dto.response.TransactionResponse;
import com.myCompany.myWalletApp.dto.response.WalletResponse;
import com.myCompany.myWalletApp.entity.Customer;
import com.myCompany.myWalletApp.entity.Transaction;
import com.myCompany.myWalletApp.entity.Wallet;
import com.myCompany.myWalletApp.enums.Currency;
import com.myCompany.myWalletApp.enums.OppositePartyType;
import com.myCompany.myWalletApp.enums.TransactionStatus;
import com.myCompany.myWalletApp.enums.TransactionType;
import com.myCompany.myWalletApp.repository.CustomerRepository;
import com.myCompany.myWalletApp.repository.TransactionRepository;
import com.myCompany.myWalletApp.repository.WalletRepository;
import com.myCompany.myWalletApp.service.WalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Override
    public WalletResponse createWallet(CreateWalletRequest request) {
        try {
            Customer customer = customerRepository.findById(1L)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

            Wallet wallet = Wallet.builder()
                    .customer(customer)
                    .walletName(request.getWalletName())
                    .currency(Currency.valueOf(request.getCurrency()))
                    .activeForShopping(request.getActiveForShopping())
                    .activeForWithdraw(request.getActiveForWithdraw())
                    .balance(BigDecimal.ZERO)
                    .usableBalance(BigDecimal.ZERO)
                    .build();

            Wallet savedWallet = walletRepository.save(wallet);

            return WalletResponse.builder()
                    .id(savedWallet.getId())
                    .walletName(savedWallet.getWalletName())
                    .currency(savedWallet.getCurrency().name())
                    .balance(savedWallet.getBalance())
                    .usableBalance(savedWallet.getUsableBalance())
                    .activeForShopping(savedWallet.isActiveForShopping())
                    .activeForWithdraw(savedWallet.isActiveForWithdraw())
                    .build();
        } catch (EntityNotFoundException e) {
            logger.error("Error creating wallet: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while creating wallet: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while creating the wallet", e);
        }
    }

    @Override
    public List<WalletResponse> getWallets(Currency currency) {
        try {
            // Şimdilik sadece customerId = 1 için filtreleyelim
            Customer customer = customerRepository.findById(1L)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

            List<Wallet> wallets;

            if (currency != null) {
                wallets = walletRepository.findByCustomerAndCurrency(customer, currency);
            } else {
                wallets = walletRepository.findByCustomer(customer);
            }

            return wallets.stream()
                    .map(wallet -> WalletResponse.builder()
                            .id(wallet.getId())
                            .walletName(wallet.getWalletName())
                            .currency(wallet.getCurrency().name())
                            .balance(wallet.getBalance())
                            .usableBalance(wallet.getUsableBalance())
                            .activeForShopping(wallet.isActiveForShopping())
                            .activeForWithdraw(wallet.isActiveForWithdraw())
                            .build())
                    .toList();
        } catch (EntityNotFoundException e) {
            logger.error("Error retrieving wallets: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving wallets: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while retrieving wallets", e);
        }
    }

    @Override
    public TransactionResponse deposit(Long walletId, DepositRequest request) {
        try {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

            BigDecimal amount = request.getAmount();
            TransactionStatus status = amount.compareTo(BigDecimal.valueOf(1000)) > 0
                    ? TransactionStatus.PENDING
                    : TransactionStatus.APPROVED;

            Transaction transaction = Transaction.builder()
                    .wallet(wallet)
                    .amount(amount)
                    .type(TransactionType.DEPOSIT)
                    .oppositePartyType(OppositePartyType.valueOf(request.getSourceType()))
                    .oppositeParty(request.getSource())
                    .status(status)
                    .build();

            transactionRepository.save(transaction);

            wallet.setBalance(wallet.getBalance().add(amount));
            if (status == TransactionStatus.APPROVED) {
                wallet.setUsableBalance(wallet.getUsableBalance().add(amount));
            }
            walletRepository.save(wallet);

            return TransactionResponse.builder()
                    .id(transaction.getId())
                    .amount(transaction.getAmount())
                    .type(transaction.getType().name())
                    .oppositePartyType(transaction.getOppositePartyType().name())
                    .oppositeParty(transaction.getOppositeParty())
                    .status(transaction.getStatus().name())
                    .build();
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            logger.error("Error during deposit operation on wallet with ID {}: {}", walletId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during deposit operation on wallet with ID {}: {}", walletId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred during the deposit operation", e);
        }
    }

    @Override
    public TransactionResponse withdraw(Long walletId, WithdrawRequest request) {
        try {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

            if (!wallet.isActiveForWithdraw()) {
                throw new IllegalStateException("Withdraw is not allowed for this wallet");
            }

            BigDecimal amount = request.getAmount();
            if (wallet.getUsableBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient usable balance");
            }

            TransactionStatus status = amount.compareTo(BigDecimal.valueOf(1000)) > 0
                    ? TransactionStatus.PENDING
                    : TransactionStatus.APPROVED;

            Transaction transaction = Transaction.builder()
                    .wallet(wallet)
                    .amount(amount)
                    .type(TransactionType.WITHDRAW)
                    .oppositePartyType(OppositePartyType.valueOf(request.getDestinationType()))
                    .oppositeParty(request.getDestination())
                    .status(status)
                    .build();

            transactionRepository.save(transaction);

            wallet.setUsableBalance(wallet.getUsableBalance().subtract(amount));
            if (status == TransactionStatus.APPROVED) {
                wallet.setBalance(wallet.getBalance().subtract(amount));
            }

            walletRepository.save(wallet);

            return TransactionResponse.builder()
                    .id(transaction.getId())
                    .amount(transaction.getAmount())
                    .type(transaction.getType().name())
                    .oppositePartyType(transaction.getOppositePartyType().name())
                    .oppositeParty(transaction.getOppositeParty())
                    .status(transaction.getStatus().name())
                    .build();
        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            logger.error("Error during withdraw operation on wallet with ID {}: {}", walletId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during withdraw operation on wallet with ID {}: {}", walletId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred during the withdraw operation", e);
        }
    }

    @Override
    public List<TransactionResponse> getTransactions(Long walletId) {
        try {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

            List<Transaction> transactions = transactionRepository.findByWallet(wallet);

            return transactions.stream()
                    .map(tx -> TransactionResponse.builder()
                            .id(tx.getId())
                            .amount(tx.getAmount())
                            .type(tx.getType().name())
                            .oppositePartyType(tx.getOppositePartyType().name())
                            .oppositeParty(tx.getOppositeParty())
                            .status(tx.getStatus().name())
                            .build())
                    .toList();
        } catch (EntityNotFoundException e) {
            logger.error("Error retrieving transactions for wallet with ID {}: {}", walletId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving transactions for wallet with ID {}: {}", walletId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while retrieving transactions", e);
        }
    }
}