package com.vsn.services.impl;

import com.vsn.dto.SendDTO;
import com.vsn.entities.registration.User;
import com.vsn.entities.wallets.Currency;
import com.vsn.entities.wallets.Wallet;
import com.vsn.exceptions.WrongBalanceException;
import com.vsn.repositories.WalletRepository;
import com.vsn.services.impl.node_services.USDTnodeServiceImpl;
import com.vsn.services.interfaces.NodeService;
import com.vsn.services.interfaces.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Service
@Log4j2
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final USDTnodeServiceImpl usdTnodeService;
    private final CurrencyPriceService currencyPriceService;


    @Override
    public Wallet createWallets(User user) {
        Wallet usdtWallet = null;
        try {
             usdtWallet = walletRepository.save(Objects.requireNonNull(getNode(Currency.USDT)).createWallet(user));
        } catch (Exception e) {
           try {
               walletRepository.delete(usdtWallet);
           }catch (Exception ex){/*ignore*/}
           throw new RuntimeException("rollback");
        }
        return null;
    }


    @Override
    public List<Wallet> getWalletsByUser(User user) {
        List<Wallet> walletList = walletRepository.getAllByUserId(user.getId());
        return walletList;
    }

    @Override
    public Wallet getWalletsByUserAndCurrency(@NotNull User user, Currency currency) {
        return walletRepository.getByUserIdAndCurrency(user.getId(), currency);
    }



    @Override
    public List<Wallet> getWalletsByCurrency(Currency currency) {
        return walletRepository.getByCurrency(currency);
    }


    @Override
    public Double getWalletBalance(@NotNull Wallet wallet) throws IOException {
        return wallet.getBalance();
    }

    @Override
    public double setUserBalance(Wallet wallet, double balance) {
        wallet.setBalance(balance);
        walletRepository.save(wallet);
        log.info(String.format("Wallet address : %s , new balance -> %f %s", wallet.getAddress(), wallet.getBalance(), wallet.getCurrency().toString()));
        return balance;
    }

    @Override
    public boolean sendToAddress(SendDTO sendDTO, User user) throws WrongBalanceException {
        Currency currency = sendDTO.getCurrency();
        Wallet userWallet = walletRepository.getByUserIdAndCurrency(user.getId(), currency);

        if (userWallet == null) {
            log.error("Not such wallet user - {} currency {}", user.getEmail(), currency);
            throw new RuntimeException("Not such wallet");
        }
        NodeService nodeService = getNode(currency);

        return !nodeService.sendToAddress(userWallet, sendDTO.getAddress(), sendDTO.getAmount()).equals("");
    }



    @SneakyThrows
    private NodeService getNode(Currency currency) {
        if (currency.equals(Currency.USDT)) {
            return usdTnodeService;
        }
        return null;
    }


}
