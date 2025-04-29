package com.myCompany.myWalletApp.repository;

import com.myCompany.myWalletApp.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Belirli bir müşteriye ait cüzdanları listele
    List<Wallet> findByCustomerId(Long customerId);

    // Currency'ye göre filtreleme yaparak cüzdan bulma
    List<Wallet> findByCurrency(String currency);

    // Cüzdan ID'sine göre cüzdan bulma
    Optional<Wallet> findById(Long id);
}
