package com.vsn.services.impl.erc20;

import org.jetbrains.annotations.NotNull;
import java.math.BigInteger;

public class ERC20Utils {
    private static final String PARITY_BALANCE_OF = "0x70a08231";
    private static final String PADDING_20_BYTES = "000000000000000000000000";

    private static final String TRANSFER = "0xa9059cbb";
    private static final String PREFIX_0X = "0x";

    @NotNull
    public static String createTransferData(String toAddress, BigInteger amountTo){
        return createData(TRANSFER, toAddress, amountTo.toString(16));
    }

    @NotNull
    public static String createBalanceData(@NotNull String address){
        return PARITY_BALANCE_OF+PADDING_20_BYTES+address.replaceFirst("0x", "");
    }

    private static String createData(String method, @NotNull String ... args){
        StringBuilder sb = new StringBuilder(method);

        for(String arg: args){
            String hex;
            if (arg.startsWith(PREFIX_0X)) hex = arg.substring(2);
            else hex = arg;
            StringBuilder argSB =new StringBuilder(hex);
            for(int i = hex.length(); i < 64; i++){
                argSB.insert(0, "0");
            }
            sb.append(argSB);
        }

        return sb.toString();
    }

}
