package com.vsn.services.impl.node_services;

import com.vsn.entities.registration.User;
import com.vsn.entities.transactions.TransactionStatus;
import com.vsn.entities.transactions.TransactionType;
import com.vsn.entities.wallets.Currency;
import com.vsn.entities.wallets.Wallet;
import com.vsn.exceptions.WrongBalanceException;
import com.vsn.repositories.WalletRepository;
import com.vsn.services.impl.erc20.ERC20Tokens;
import com.vsn.services.impl.erc20.ERC20Utils;
import com.vsn.services.interfaces.NodeService;
import com.vsn.services.interfaces.TransactionsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


import static com.vsn.services.impl.erc20.ERC20Utils.createBalanceData;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Service
@Slf4j
public class USDTnodeServiceImpl extends EthBaseService implements NodeService {

    public USDTnodeServiceImpl(WalletRepository walletRepository, TransactionsService transactionsService) {
        super(walletRepository, transactionsService);
    }

    private static final ERC20Tokens token = ERC20Tokens.USDT;
    private static BigInteger GAS_LIMIT = BigInteger.valueOf(21_000L);



    @Override
    public Wallet createWallet(@NotNull User user) throws IOException, CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        String walletName = WalletUtils.generateNewWalletFile(ethWalletPassword, new File(ethWalletDirectory));

        Credentials credentials = WalletUtils.loadCredentials(ethWalletPassword, ethWalletDirectory + "/" + walletName);

        String accountAddress = credentials.getAddress();
        String password = String.format("%s;%s", String.valueOf(credentials.getEcKeyPair().getPrivateKey()), String.valueOf(credentials.getEcKeyPair().getPublicKey()));

        Wallet wallet = new Wallet();
        wallet.setAddress(accountAddress);
        wallet.setCurrency(Currency.USDT);
        wallet.setUserId(user.getId());
        String QR_PREFIX = "https://chart.apis.google.com/chart?choe=UTF-8&chld=H&cht=qr&chs=300x300&chl=";
        wallet.setQrLink(QR_PREFIX + accountAddress);
        wallet.setPassword(password);

        log.info(String.format("Created USDT wallet: %s - user : %s", wallet.getAddress(), user.getEmail()));

        return wallet;
    }


    @Override
    public String sendToAddress(Wallet wallet, String addressTo, double amountDouble) throws WrongBalanceException {
        log.info("Invoked send to {} in amount {}", addressTo, amountDouble);
        return  "";
    }

    @Override
    public Double getBalanceWallet(@NotNull Wallet wallet) {
        return wallet.getBalance();
    }

    private void transferERC20Token(String from, String to, BigInteger value) throws IOException {
        log.info(token + " Tokens " + value + " from address {} sent to {}", from, to);

        Credentials fromWalletCredentials = getCredentials(from);
        BigInteger gasPrice = getGasPrice();
        BigInteger gasLimit = GAS_LIMIT;

        log.info("Gas price: " + gasPrice);
        log.info("Gas limit: " + GAS_LIMIT);
        RawTransaction rawTransaction = getRawTransaction(from, to, value, token.contractAddress, gasLimit, gasPrice);

        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, fromWalletCredentials);
        String hexValue = Numeric.toHexString(signMessage);
        //Send the transaction
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

        if(ethSendTransaction.getTransactionHash() != null) {
            log.info("Send transaction: {}", ethSendTransaction);

            log.debug("Funds " + value + " sent to main account!!!");

            String hash = ethSendTransaction.getTransactionHash();

            transactionsService.saveTransaction(
                    com.vsn.entities.transactions.Transaction.builder()
                            .hash(hash)
                            .amount(value)
                            .currency(Currency.USDT)
                            .status(TransactionStatus.SUCCESS)
                            .type(TransactionType.WITHDRAW)
                            .userId(walletRepository.getWalletByAddress(from).
                                    orElseThrow(()-> new RuntimeException("Wallet is not registered")).getUserId())
                            .build());
        } else {
            throw new  RuntimeException("Error transfer to main address");
        }
    }


    private BigInteger getTokenBalance(String address) {
        try {
            String respBalance = web3j
                    .ethCall(new org.web3j.protocol.core.methods.request.Transaction
                            (null, null, null, null, token.contractAddress, null,
                                    createBalanceData(address)), LATEST).send().getValue();

            return  "0x".equals(respBalance) ? BigInteger.ZERO : new BigInteger((respBalance).substring(2), 16);
        } catch ( IOException e){
            log.error("Don`t write balance {}",address);
            return BigInteger.ZERO;
        }
    }

    @SneakyThrows
    private RawTransaction getRawTransaction(String from, String to, BigInteger value, String contractAddress, BigInteger gasLimit, BigInteger gasPrice) {
        BigInteger nonce = web3j.ethGetTransactionCount(from, LATEST).send().getTransactionCount();


        String transferData = ERC20Utils.createTransferData(to, value);

        return RawTransaction.createTransaction(nonce, gasPrice, gasLimit,
                contractAddress, BigInteger.ZERO, transferData);
    }


    @SneakyThrows
    private BigInteger getGasPrice(){
       return web3j.ethGasPrice().send().getGasPrice();
    }
}