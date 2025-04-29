package com.myCompany.myWalletApp.repository;

import com.myCompany.myWalletApp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Müşteriyi TCKN'ye göre bul
    Optional<Customer> findByTckn(String tckn);

    // Müşteri ID'sine göre müşteri bulma
    Optional<Customer> findById(Long id);
}
