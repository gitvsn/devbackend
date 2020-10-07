package com.vsn.repositories;

import com.vsn.entities.transactions.Transaction;
import com.vsn.entities.transactions.TransactionType;
import com.vsn.entities.wallets.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionsRepository  extends CrudRepository<Transaction, String> {
    List <Transaction> findAll();
    List <Transaction> getByUserId(Long userId);
    List <Transaction> getByUserIdAndCurrency(Long userId, Currency currency);
    List <Transaction> getByUserIdAndCurrencyOrCurrency(Long userId, Currency currency,Currency currency2);
    List <Transaction> getByCurrency(Currency currency);
    List <Transaction> getAllByCurrencyAndType(Currency currency, TransactionType transactionType);
}
