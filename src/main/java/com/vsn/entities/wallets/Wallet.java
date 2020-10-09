package com.vsn.entities.wallets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsn.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallets")
public class Wallet extends BaseEntity {


    @Column(name = "address")
    private String address;

    @JsonIgnore
    @Column(name = "user_id")
    private long userId;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "qr_link")
    private String qrLink;


    @Column(nullable = false, precision = 19, scale = 8,name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @JsonIgnore
    @Column(name = "password")
    private String password;


}
