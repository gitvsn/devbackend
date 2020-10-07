package com.vsn.entities.registration;

import com.vsn.entities.BaseEntity;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "id"}))
@Cacheable(false)
@ToString
@Data
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
}
