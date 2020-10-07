package com.vsn.entities.registration;

import com.vsn.entities.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "token")
public class UserToken extends BaseEntity {
    private long userId;
    private String token;
}
