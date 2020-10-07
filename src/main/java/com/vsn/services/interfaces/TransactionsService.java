package com.vsn.services.interfaces;

import com.vsn.entities.registration.User;
import com.vsn.entities.transactions.Transaction;
import com.vsn.entities.transactions.TransactionType;
import com.vsn.entities.wallets.Currency;

import java.util.List;

public interface TransactionsService {

    List <Transaction> getAll();

    List <Transaction> getByUser(User user);

    Transaction saveTransaction(Transaction transaction);

    List <Transaction> getByUserAndCurrency(Long userId, Currency currency);

    List <Transaction> getByUserAndCurrency(Long userId, Currency... currency);

    List <Transaction> getByCurrency(Currency currency);

    List <Transaction> getByCurrencyAndTxType(Currency currency, TransactionType transactionType);
}
