package com.myCompany.myWalletApp.service;

import com.myCompany.myWalletApp.dto.request.ApproveTransactionRequest;
import com.myCompany.myWalletApp.dto.response.TransactionResponse;

public interface TransactionService {

    TransactionResponse approveOrDenyTransaction(Long transactionId, ApproveTransactionRequest request);
}
