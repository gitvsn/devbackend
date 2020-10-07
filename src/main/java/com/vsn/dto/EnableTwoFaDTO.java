package com.vsn.dto;

import lombok.Data;

@Data
public class EnableTwoFaDTO {
    private String password;
    private String code;
}
