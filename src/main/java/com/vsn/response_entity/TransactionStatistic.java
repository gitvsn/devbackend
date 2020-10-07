package com.vsn.response_entity;

import com.vsn.entities.transactions.TransactionStatus;
import com.vsn.entities.transactions.TransactionType;
import com.vsn.entities.wallets.Currency;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionStatistic {
    private String email;
    private Currency currency;
    private TransactionType type;
    private Double amount;
    private Double fee;
    private String address;
    private TransactionStatus status;
    private Long date;
}
