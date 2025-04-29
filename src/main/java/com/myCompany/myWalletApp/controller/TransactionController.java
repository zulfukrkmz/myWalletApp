package com.myCompany.myWalletApp.controller;

import com.myCompany.myWalletApp.dto.request.ApproveTransactionRequest;
import com.myCompany.myWalletApp.dto.response.TransactionResponse;
import com.myCompany.myWalletApp.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{transactionId}/approve")
    public ResponseEntity<TransactionResponse> approveTransaction(
            @PathVariable Long transactionId,
            @Valid @RequestBody ApproveTransactionRequest request) {

        TransactionResponse response = transactionService.approveOrDenyTransaction(transactionId, request);
        return ResponseEntity.ok(response);
    }
}
