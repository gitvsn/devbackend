package com.vsn.repositories;

import com.vsn.entities.wallets.Currency;
import com.vsn.entities.wallets.Wallet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends CrudRepository<Wallet, Long> {
    List <Wallet> getAllByUserId(Long userId);
    List <Wallet> getByCurrency(Currency currency);
    Wallet getByUserIdAndCurrency(Long userId,Currency currency);
    Optional<Wallet> getWalletByAddress(String address);
}
