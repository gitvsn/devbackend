package com.vsn.services.impl;

import com.vsn.entities.registration.User;
import com.vsn.entities.transactions.Transaction;
import com.vsn.entities.transactions.TransactionStatus;
import com.vsn.entities.transactions.TransactionType;
import com.vsn.entities.wallets.Currency;
import com.vsn.repositories.TransactionsRepository;
import com.vsn.services.interfaces.TransactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.vsn.entities.transactions.TransactionType.WITHDRAW;

@Slf4j
@Service
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
    public List<Transaction> getByStatus(TransactionStatus status) {
        return  transactionsRepository.getAllByStatus(status);
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

    @Override
    public Map<String, BigDecimal> getTransactionsAmount(User user) {
        Map<String, BigDecimal> trInfoMap = new HashMap<>();
        BigDecimal withdraw = transactionsRepository.getSumBuyWithdraw(user.getId()) == null ?
                new BigDecimal(0) : transactionsRepository.getSumBuyWithdraw(user.getId());

        BigDecimal deposit = transactionsRepository.getSumBuyDeposit(user.getId()) == null ?
                new BigDecimal(0) : transactionsRepository.getSumBuyDeposit(user.getId());

        trInfoMap.put("withdraw", withdraw);
        trInfoMap.put("deposit", deposit);

        return  trInfoMap;
    }

    @Override
    public Map<Long, BigDecimal> getTransactionsData(User user) {
        Map<Long, BigDecimal> trInfoMap = new HashMap<>();
        BigDecimal balance = new BigDecimal(0);
        trInfoMap.put(user.getCreated(), balance);

        List<Transaction> trList = transactionsRepository.getByUserIdAndStatus(user.getId(), TransactionStatus.SUCCESS);
        if(trList != null){
            for (Transaction transaction : trList) {
                if (transaction.getType()== TransactionType.WITHDRAW) {
                    balance = balance.subtract(transaction.getAmount());
                } else if (transaction.getType()== TransactionType.DEPOSIT) {
                    balance = balance.add(transaction.getAmount());
                }
                trInfoMap.put(transaction.getCreated(), balance);
            }
        }

        return  trInfoMap;
    }

}
