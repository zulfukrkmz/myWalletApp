package com.myCompany.myWalletApp.controller;

import com.myCompany.myWalletApp.dto.request.CreateWalletRequest;
import com.myCompany.myWalletApp.dto.request.DepositRequest;
import com.myCompany.myWalletApp.dto.request.WithdrawRequest;
import com.myCompany.myWalletApp.dto.response.TransactionResponse;
import com.myCompany.myWalletApp.dto.response.WalletResponse;
import com.myCompany.myWalletApp.enums.Currency;
import com.myCompany.myWalletApp.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        return ResponseEntity.ok(walletService.createWallet(request));
    }

    @GetMapping
    public ResponseEntity<List<WalletResponse>> listWallets(@RequestParam(required = false) Currency currency) {
        return ResponseEntity.ok(walletService.getWallets(currency));
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable Long walletId, @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(walletService.deposit(walletId, request));
    }

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@PathVariable Long walletId, @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(walletService.withdraw(walletId, request));
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getTransactions(walletId));
    }
}
