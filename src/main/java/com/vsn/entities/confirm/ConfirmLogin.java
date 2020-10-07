package com.vsn.entities.confirm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsn.entities.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "confirm_login")
public class ConfirmLogin extends BaseEntity {

    @Column(name = "code")
    private String code;

    @JsonIgnore
    @Column(name = "user_id")
    private long userId;
}