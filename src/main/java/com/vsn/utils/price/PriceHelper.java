package com.vsn.utils.price;

import org.jetbrains.annotations.Contract;

public class PriceHelper {
    // 1%
    public static double SYSTEM_FEE = 0.01;

    @Contract(pure = true)
    public static double getSystemFee(double amount){
        return amount-(amount*SYSTEM_FEE);
    }
}
