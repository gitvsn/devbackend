package com.vsn.entities.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsn.entities.BaseEntity;
import lombok.*;

import javax.persistence.*;


@RequiredArgsConstructor
@Entity
@Data
@Table(name = "users_info", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "id"}))
public class UserInfo extends BaseEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "country")
    private String country;

    @Column(name = "surname")
    private String surname;

    @Column(name = "phone")
    private String phone;

    @JsonIgnore
    @Column(name = "user_id")
    private long userId;

    @Column(name = "secret")
    private String secret;

    @Column(name = "two_link")
    private String twoFaLink;

    @Column(name = "twoFaEnable")
    private boolean twoFaEnable;

}
