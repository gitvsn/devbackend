package com.vsn.response_entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AvatarRequest {

    private MultipartFile document;
}