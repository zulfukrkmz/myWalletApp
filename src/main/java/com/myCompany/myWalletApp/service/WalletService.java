package com.myCompany.myWalletApp.service;

import com.myCompany.myWalletApp.dto.request.CreateWalletRequest;
import com.myCompany.myWalletApp.dto.request.DepositRequest;
import com.myCompany.myWalletApp.dto.request.WithdrawRequest;
import com.myCompany.myWalletApp.dto.response.TransactionResponse;
import com.myCompany.myWalletApp.dto.response.WalletResponse;
import com.myCompany.myWalletApp.enums.Currency;

import java.util.List;

public interface WalletService {

    WalletResponse createWallet(CreateWalletRequest request);

    List<WalletResponse> getWallets(Currency currency);

    TransactionResponse deposit(Long walletId, DepositRequest request);

    TransactionResponse withdraw(Long walletId, WithdrawRequest request);

    List<TransactionResponse> getTransactions(Long walletId);
}
