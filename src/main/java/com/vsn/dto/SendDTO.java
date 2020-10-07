package com.vsn.dto;

import com.vsn.entities.wallets.Currency;
import lombok.Data;

@Data
public class SendDTO {
    private Currency currency;
    private double amount;
    private String address;
}
