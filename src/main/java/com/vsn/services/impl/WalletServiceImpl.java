package com.vsn.services.impl;

import com.vsn.dto.SendDTO;
import com.vsn.entities.registration.User;
import com.vsn.entities.wallets.Currency;
import com.vsn.entities.wallets.Wallet;
import com.vsn.exceptions.WrongBalanceException;
import com.vsn.repositories.WalletRepository;
import com.vsn.services.impl.node_services.VsnNodeServiceImpl;
import com.vsn.services.interfaces.NodeService;
import com.vsn.services.interfaces.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final VsnNodeServiceImpl vsnNodeService;
    private final CurrencyPriceService currencyPriceService;


    @Override
    public Wallet createWallets(User user) {
        Wallet usdtWallet = null;
        try {
             usdtWallet = walletRepository.save(Objects.requireNonNull(getNode(Currency.VSN)).createWallet(user));
        } catch (Exception e) {
            e.printStackTrace();
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
    public BigDecimal getWalletBalance(@NotNull Wallet wallet) throws IOException {
        return wallet.getBalance();
    }

    @Override
    public BigDecimal setUserBalance(Wallet wallet, BigDecimal balance) {
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
        if (currency.equals(Currency.VSN)) {
            return vsnNodeService;
        }
        return null;
    }


}
