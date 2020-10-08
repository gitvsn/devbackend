package com.vsn.entities.registration;

import com.vsn.entities.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "user_token")
public class UserToken extends BaseEntity {
    @Column(name = "user_id")
    private long userId;

    @Column(name = "user_token")
    private String token;
}
