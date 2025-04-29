package com.myCompany.myWalletApp.dto.response;

import java.math.BigDecimal;

public class WalletResponse {

    private Long id;
    private String walletName;
    private String currency;
    private boolean activeForShopping;
    private boolean activeForWithdraw;
    private BigDecimal balance;
    private BigDecimal usableBalance;
}
