package com.vsn.entities.wallets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsn.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name = "balance")
    private double balance;

    @JsonIgnore
    @Column(name = "password")
    private String password;


}
