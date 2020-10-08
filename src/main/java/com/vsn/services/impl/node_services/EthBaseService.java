package com.vsn.services.impl.node_services;

import com.vsn.entities.wallets.Wallet;
import com.vsn.repositories.WalletRepository;
import com.vsn.services.interfaces.TransactionsService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

@Service
@Log4j2
public class EthBaseService {

    @Autowired
    public String ethWalletDirectory;
    @Autowired
    public String ethWalletPassword;
    public Web3j web3j;

    final WalletRepository walletRepository;
    final TransactionsService transactionsService;


    public EthBaseService(WalletRepository walletRepository, TransactionsService transactionsService) {
        this.web3j = null;
        this.walletRepository = walletRepository;
        this.transactionsService = transactionsService;
    }



    @SneakyThrows
    Web3j connectionPool() {
        log.info("Connect to node");
        Web3j testNode = Web3j.build(new HttpService("http://134.122.52.134:8545"));
        testNode.ethBlockNumber().send().getBlockNumber();
        return testNode;
    }


    Credentials getCredentials(String address){
        Wallet wallet = this.walletRepository.getWalletByAddress(address).orElse(null);

        String[] split = wallet.getPassword().split(";");
        return Credentials.create(new ECKeyPair(new BigInteger(split[0]),
                new BigInteger(split[1])));
    }

    Credentials getCredentials(@NotNull Wallet wallet){
        String[] split = wallet.getPassword().split(";");
        return Credentials.create(new ECKeyPair(new BigInteger(split[0]),
                new BigInteger(split[1])));
    }

}
