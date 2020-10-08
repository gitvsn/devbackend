package com.vsn.services.interfaces;

import com.vsn.entities.registration.User;
import com.vsn.entities.wallets.Wallet;
import com.vsn.exceptions.WrongBalanceException;
import org.web3j.crypto.CipherException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


public interface NodeService {

    Wallet createWallet(User user) throws IOException, CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;

    Double getBalanceWallet(Wallet  wallet) throws IOException;

    String sendToAddress(Wallet wallet,String address,double amount) throws WrongBalanceException;

}