package com.vsn.response_entity;

import com.vsn.entities.wallets.Currency;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletMoreInfo {
    private Currency currency;
    private double usdPrice;
    private double usdTotal;
    private double priceChange;
}
