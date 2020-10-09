package com.vsn.services.impl.node_services;

import com.vsn.entities.registration.User;
import com.vsn.entities.transactions.TransactionStatus;
import com.vsn.entities.transactions.TransactionType;
import com.vsn.entities.wallets.Currency;
import com.vsn.entities.wallets.Wallet;
import com.vsn.exceptions.NotEnoughGas;
import com.vsn.exceptions.WrongBalanceException;
import com.vsn.repositories.WalletRepository;
import com.vsn.services.impl.erc20.ERC20Tokens;
import com.vsn.services.impl.erc20.ERC20Utils;
import com.vsn.services.interfaces.NodeService;
import com.vsn.services.interfaces.TransactionsService;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import static com.vsn.services.impl.erc20.ERC20Utils.createBalanceData;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Service
@Slf4j
public class VsnNodeServiceImpl extends EthBaseService implements NodeService {

    public VsnNodeServiceImpl(WalletRepository walletRepository, TransactionsService transactionsService) {
        super(walletRepository, transactionsService);
    }

    private static final ERC20Tokens token = ERC20Tokens.VSN;
    private static final Currency currency = ERC20Tokens.convertToCurrency(token);




    @Override
    public Wallet createWallet(@NotNull User user) throws IOException, CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        String walletName = WalletUtils.generateNewWalletFile(ethWalletPassword, new File(ethWalletDirectory));

        Credentials credentials = WalletUtils.loadCredentials(ethWalletPassword, ethWalletDirectory + "/" + walletName);

        String accountAddress = credentials.getAddress();
        String password = String.format("%s;%s", (credentials.getEcKeyPair().getPrivateKey()), (credentials.getEcKeyPair().getPublicKey()));

        Wallet wallet = new Wallet();
        wallet.setAddress(accountAddress);
        wallet.setCurrency(currency);
        wallet.setUserId(user.getId());
        String QR_PREFIX = "https://chart.apis.google.com/chart?choe=UTF-8&chld=H&cht=qr&chs=300x300&chl=";
        wallet.setQrLink(QR_PREFIX + accountAddress);
        wallet.setPassword(password);

        log.info(String.format("Created %s wallet: %s - user : %s", currency,wallet.getAddress(), user.getEmail()));

        return wallet;
    }


    @Override
    public String sendToAddress(Wallet wallet, String addressTo, double amountDouble) throws  IOException, NotEnoughGas {
        log.info("Invoked send to {} in amount {}", addressTo, amountDouble);

        BigInteger amount = BigDecimal.valueOf(amountDouble).toBigInteger();

        transferERC20Token(wallet,addressTo,amount);
        return  "";
    }

    @Override
    public BigDecimal getBalanceWallet(@NotNull Wallet wallet) {
        return wallet.getBalance();
    }

    private void transferERC20Token(Wallet fromWallet, String to, BigInteger value) throws IOException, NotEnoughGas {

        log.info(token + " Tokens " + value + " from address {} sent to {}", fromWallet.getAddress(), to);

        if(getFee().compareTo(getEthBalance(fromWallet.getAddress())) > 0){
            throw new NotEnoughGas("Not enough gas " + getFee());
        }

        Credentials fromWalletCredentials = getCredentials(fromWallet);
        BigInteger gasPrice = getGasPrice();
        BigInteger gasLimit = getGasLimit();

        log.info("Gas price: " + gasPrice);
        log.info("Gas limit: " + gasLimit);
        RawTransaction rawTransaction = getRawTransaction(fromWallet.getAddress(), to, value, token.contractAddress, gasLimit, gasPrice);

        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, fromWalletCredentials);
        String hexValue = Numeric.toHexString(signMessage);
        //Send the transaction
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

        if(ethSendTransaction.getTransactionHash() != null) {
            log.info("Send transaction: {}", ethSendTransaction);

            log.debug("Funds " + value + " sent to "+ to+" account!!!");

            String hash = ethSendTransaction.getTransactionHash();
            fromWallet.setBalance(fromWallet.getBalance().subtract(new BigDecimal(value)));


            walletRepository.save(fromWallet);
            transactionsService.saveTransaction(
                    com.vsn.entities.transactions.Transaction.builder()
                            .hash(hash)
                            .amount(new BigDecimal(value))
                            .currency(currency)
                            .status(TransactionStatus.SUCCESS)
                            .type(TransactionType.WITHDRAW)
                            .userId(fromWallet.getUserId())
                            .build());
        } else {
            throw new  RuntimeException("Error transfer");
        }
    }

    private void transferERC20Token(String from, String to, BigInteger value) throws IOException, NotEnoughGas {
        Wallet fromWallet = walletRepository.getWalletByAddress(from)
                .orElseThrow(() -> new RuntimeException("Not found from wallet"));


        log.info(token + " Tokens " + value + " from address {} sent to {}", from, to);

        if(getFee().compareTo(getEthBalance(from)) > 0){
            throw new NotEnoughGas("Not enough gas " + getFee());
        }

        Credentials fromWalletCredentials = getCredentials(from);
        BigInteger gasPrice = getGasPrice();
        BigInteger gasLimit = getGasLimit();

        log.info("Gas price: " + gasPrice);
        log.info("Gas limit: " + gasLimit);
        RawTransaction rawTransaction = getRawTransaction(from, to, value, token.contractAddress, gasLimit, gasPrice);

        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, fromWalletCredentials);
        String hexValue = Numeric.toHexString(signMessage);
        //Send the transaction
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

        if(ethSendTransaction.getTransactionHash() != null) {
            log.info("Send transaction: {}", ethSendTransaction);

            log.debug("Funds " + value + " sent to "+ to+" account!!!");

            String hash = ethSendTransaction.getTransactionHash();
            fromWallet.setBalance(fromWallet.getBalance().subtract(new BigDecimal(value)));


            walletRepository.save(fromWallet);
            transactionsService.saveTransaction(
                    com.vsn.entities.transactions.Transaction.builder()
                            .hash(hash)
                            .amount(new BigDecimal(value))
                            .currency(currency)
                            .status(TransactionStatus.SUCCESS)
                            .type(TransactionType.WITHDRAW)
                            .userId(fromWallet.getUserId())
                            .build());
        } else {
            throw new  RuntimeException("Error transfer");
        }
    }


    private BigDecimal getTokenBalance(String address) {
        try {
            String respBalance = web3j
                    .ethCall(new org.web3j.protocol.core.methods.request.Transaction
                            (null, null, null, null, token.contractAddress, null,
                                    createBalanceData(address)), LATEST).send().getValue();

            return  "0x".equals(respBalance) ? BigDecimal.ZERO : new BigDecimal(new BigInteger((respBalance).substring(2), 16));
        } catch ( IOException e){
            log.error("Don`t write balance {}",address);
            return BigDecimal.ZERO;
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

    @SneakyThrows
    private BigInteger getGasLimit(){
        return  new BigDecimal(50000D).toBigInteger();
     //  return web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST,true).send().getBlock().getGasLimit();
    }

    @SneakyThrows
    private BigInteger getFee() {
        BigInteger totalGas = getGasLimit().multiply(getGasPrice());
        return (Convert.fromWei(totalGas.toString(), Convert.Unit.ETHER)).toBigInteger();
    }

    @NotNull
    private BigInteger getEthBalance(String address) throws IOException {
        return  Convert.fromWei(String.valueOf(web3j.ethGetBalance(address,
                DefaultBlockParameterName.LATEST).send().getBalance()),
                Convert.Unit.ETHER)
                .toBigInteger();
    }


    @SneakyThrows
    @PostConstruct
    private void runERC20Observe(){
        // TODO test data
//        System.out.println(getTokenBalance("0x4a7d4f98ea6a8047310a2ff8ac2167ecd37a7ef8")
//                .divide(new BigDecimal("1000000000000000000"),4, RoundingMode.FLOOR));

        web3j.ethLogFlowable(getFilterRequest()).subscribe(log -> {
            TokenTransaction tx = new TokenTransaction().fillByFilterLog(log);

            if (getAccounts().contains(tx.to)) {
                processDepositWallet(tx);
            }
        });
    }

    private final Event TRANSFER_EVENT = new Event("Transfer",
            List.of(new TypeReference<Address>(true) {},
                    new TypeReference<Address>(true) {},
                    new TypeReference<Uint256>(false) {}));


    private EthFilter getFilterRequest() {
        // TODO test data
       //  DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(new BigInteger("11021191"));

        EthFilter filter = new EthFilter(LATEST, LATEST, token.contractAddress);
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return filter;
    }

    @Cacheable(value = "erc20_accounts")
    public Set<String> getAccounts(){
        return  walletRepository.getByCurrency(currency)
                .stream()
                .map(Wallet::getAddress)
                .collect(Collectors.toSet());
    }

    private BigDecimal getDecNumber(BigDecimal value){
        return  value.divide(new BigDecimal("1000000000000000000"),4, RoundingMode.FLOOR);
    }


    @Transactional
    void processDepositWallet(TokenTransaction transaction){
        log.info(" =======  Deposit tx ======");
        log.info("{}",transaction);

        Wallet depositWallet = walletRepository.getWalletByAddress(transaction.to)
                .orElseThrow(()->new RuntimeException("Deposit wallet not found"));

        log.info("Deposit wallet old balance -> {}",depositWallet.getBalance());
        BigDecimal newBalance = depositWallet.getBalance().add(transaction.amount);
        log.info("Deposit wallet new balance -> {}{} (+{})",depositWallet.getBalance(),currency,transaction.amount);

        depositWallet.setBalance(newBalance);

        walletRepository.save(depositWallet);
        transactionsService.saveTransaction(
                com.vsn.entities.transactions.Transaction.builder()
                        .hash(transaction.hash)
                        .amount(transaction.amount)
                        .currency(currency)
                        .status(TransactionStatus.SUCCESS)
                        .type(TransactionType.DEPOSIT)
                        .userId(depositWallet.getUserId())
                        .build());
    }



    @ToString
    private class TokenTransaction {
        private String hash;
        private String contract;
        private String from;
        private String to;
        private BigDecimal amount;
        private BigInteger blockNumber;


        public TokenTransaction fillByFilterLog(Log log){
            List<String> topics = log.getTopics();
            this.hash = log.getTransactionHash();
            this.contract = log.getAddress();
            this.from = "0x" + topics.get(1).substring(26);
            this.to = "0x" + topics.get(2).substring(26);
            this.amount = getDecNumber(new BigDecimal(new BigInteger(log.getData().substring(2), 16)));
            this.blockNumber = log.getBlockNumber();

            return this;
        }


    }

}