package com.vsn.services.interfaces;

import com.vsn.dto.SendDTO;
import com.vsn.entities.registration.User;
import com.vsn.entities.wallets.Currency;
import com.vsn.entities.wallets.Wallet;
import com.vsn.exceptions.NotEnoughGas;
import com.vsn.exceptions.WrongBalanceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface WalletService {
    Wallet createWallets(User user) throws IOException;

    List<Wallet> getWalletsByUser(User user);

    Wallet getWalletsByUserAndCurrency(User user,Currency currency);


    List<Wallet> getWalletsByCurrency(Currency currency);

    BigDecimal getWalletBalance(Wallet wallet) throws IOException;

    BigDecimal setUserBalance(Wallet wallet,BigDecimal balance);

    boolean sendToAddress(SendDTO sendDTO, User user) throws WrongBalanceException, IOException, NotEnoughGas;


}
