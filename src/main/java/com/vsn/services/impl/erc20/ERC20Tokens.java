package com.vsn.services.impl.erc20;


import com.vsn.entities.wallets.Currency;
import lombok.AllArgsConstructor;
import org.bouncycastle.crypto.RuntimeCryptoException;

import java.util.stream.Stream;

@AllArgsConstructor
public enum ERC20Tokens {


    USDT("0xdac17f958d2ee523a2206206994597c13d831ec7", 6);

    public final String contractAddress;
    public final int decimal;

    public static ERC20Tokens getTokenName(String contractAddress){
        return Stream.of(ERC20Tokens.values())
                .filter(t -> t.contractAddress.equals(contractAddress))
                .findFirst().orElseThrow(RuntimeCryptoException::new);
    }

    public static ERC20Tokens getToken(Currency currency) {
        return Stream.of(ERC20Tokens.values())
                .filter(tokenn -> currency.name().equals(tokenn.name()))
                .findFirst().orElseThrow(RuntimeCryptoException::new);
    }

    public static Currency convertToCurrency(ERC20Tokens token){
        return Stream.of(Currency.values())
                .filter(curr -> curr.name().equals(token.name()))
                .findFirst().orElseThrow(RuntimeCryptoException::new);
    }

    public static boolean isERC20Currency(Currency currency){
        return  Stream.of(ERC20Tokens.values())
                .anyMatch(token -> token.name().equals(currency.name()));
    }


}
