package com.vsn.response_entity;

import com.vsn.entities.registration.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserStatistic  {
    private long id;
    private String fullName;
    private String phone;
    private String country;
    private Long  registerDate;
    private UserStatus status;
    private Map balances;
}
