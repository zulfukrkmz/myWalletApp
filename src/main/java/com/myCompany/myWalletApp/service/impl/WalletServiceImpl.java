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

    @Override
    public WalletResponse createWallet(CreateWalletRequest request) {

        // 1. Müşteri bilgisi alınacak. (Şu anlık müşteri id nasıl geliyor netleşmediği için customer'ı varsayalım ya da hazır müşteri alalım.)
        // Gerçek sistemde kimlik doğrulama sonrası CustomerId alınırdı (SecurityContext gibi). Şimdi manuel alıyoruz.

        // ÖRNEK: Dummy bir customerId ile çalışalım şimdilik (örneğin id = 1L).
        Customer customer = customerRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        // 2. Wallet entity oluşturulacak
        Wallet wallet = Wallet.builder()
                .customer(customer)
                .walletName(request.getWalletName())
                .currency(Currency.valueOf(request.getCurrency()))
                .activeForShopping(request.getActiveForShopping())
                .activeForWithdraw(request.getActiveForWithdraw())
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .build();

        // 3. Kaydet
        Wallet savedWallet = walletRepository.save(wallet);

        // 4. Response DTO'ya map edip döndür
        return WalletResponse.builder()
                .id(savedWallet.getId())
                .walletName(savedWallet.getWalletName())
                .currency(savedWallet.getCurrency().name())
                .balance(savedWallet.getBalance())
                .usableBalance(savedWallet.getUsableBalance())
                .activeForShopping(savedWallet.isActiveForShopping())
                .activeForWithdraw(savedWallet.isActiveForWithdraw())
                .build();
    }


    @Override
    public List<WalletResponse> getWallets(Currency currency) {
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
    }


    @Override
    public TransactionResponse deposit(Long walletId, DepositRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        BigDecimal amount = request.getAmount();

        // Transaction durumu belirleme
        TransactionStatus status = amount.compareTo(BigDecimal.valueOf(1000)) > 0
                ? TransactionStatus.PENDING
                : TransactionStatus.APPROVED;

        // Transaction oluştur
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .oppositePartyType(OppositePartyType.valueOf(request.getSourceType()))
                .oppositeParty(request.getSource())
                .status(status)
                .build();

        transactionRepository.save(transaction);

        // Cüzdanın bakiyesini güncelle
        wallet.setBalance(wallet.getBalance().add(amount));
        if (status == TransactionStatus.APPROVED) {
            wallet.setUsableBalance(wallet.getUsableBalance().add(amount));
        }
        walletRepository.save(wallet);

        // Transaction response dön
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .oppositePartyType(transaction.getOppositePartyType().name())
                .oppositeParty(transaction.getOppositeParty())
                .status(transaction.getStatus().name())
                .build();
    }


    @Override
    public TransactionResponse withdraw(Long walletId, WithdrawRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        // Eğer cüzdan ayarları izin vermiyorsa hata döndür
        if (!wallet.isActiveForWithdraw()) {
            throw new IllegalStateException("Withdraw is not allowed for this wallet");
        }

        BigDecimal amount = request.getAmount();

        // Kullanılabilir bakiye yeterli mi?
        if (wallet.getUsableBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient usable balance");
        }

        // Transaction durumu belirleme
        TransactionStatus status = amount.compareTo(BigDecimal.valueOf(1000)) > 0
                ? TransactionStatus.PENDING
                : TransactionStatus.APPROVED;

        // Transaction oluştur
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .oppositePartyType(OppositePartyType.valueOf(request.getDestinationType()))
                .oppositeParty(request.getDestination())
                .status(status)
                .build();

        transactionRepository.save(transaction);

        // Bakiye güncellemesi
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
    }


    @Override
    public List<TransactionResponse> getTransactions(Long walletId) {
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
    }
}