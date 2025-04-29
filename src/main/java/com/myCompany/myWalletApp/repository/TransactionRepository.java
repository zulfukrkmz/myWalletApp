package com.myCompany.myWalletApp.repository;

import com.myCompany.myWalletApp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Cüzdana ait tüm işlemleri listele
    List<Transaction> findByWalletId(Long walletId);

    // Cüzdan ve işlem tipi (deposit/withdraw) ile işlem arama
    List<Transaction> findByWalletIdAndType(Long walletId, String type);
}
