package com.vsn.services.impl;

import com.vsn.entities.registration.User;
import com.vsn.entities.transactions.Transaction;
import com.vsn.entities.transactions.TransactionType;
import com.vsn.entities.wallets.Currency;
import com.vsn.repositories.TransactionsRepository;
import com.vsn.services.interfaces.TransactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionsRepository transactionsRepository;

    @Override
    public List<Transaction> getAll() {
        return  transactionsRepository.findAll();
    }


    @Override
    public List<Transaction> getByUser(User user) {
        return  transactionsRepository.getByUserId(user.getId());
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return  transactionsRepository.save(transaction);
    }

    @Override
    public List<Transaction> getByUserAndCurrency(Long userId, Currency currency) {
        return transactionsRepository.getByUserIdAndCurrency(userId,currency);
    }

    @Override
    public List <Transaction> getByUserAndCurrency(Long userId, Currency... currency) {
        return transactionsRepository.getByUserIdAndCurrencyOrCurrency(userId,currency[0],currency[1]);
    }

    @Override
    public List<Transaction> getByCurrency(Currency currency) {
        return transactionsRepository.getByCurrency(currency);
    }

    @Override
    public List<Transaction> getByCurrencyAndTxType(Currency currency, TransactionType transactionType) {
        return  transactionsRepository.getAllByCurrencyAndType(currency,transactionType);
    }

}
