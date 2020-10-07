package com.vsn.services.interfaces;

import com.vsn.entities.registration.Avatar;
import com.vsn.entities.registration.User;
import com.vsn.entities.registration.UserInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

public interface UserInfoService {
    void saveAvatar(Avatar avatar);
    Optional<Avatar> loadAvatar(User user);
    void removeAvatar(Long userId);
    byte [] convertImageToByteArray(MultipartFile multipartFile);

    UserInfo saveUserInfo(UserInfo userInfo);
    UserInfo getByUser(User user);

    void enableTwoFa(User user);
    void disableTwoFa(User user);
    boolean twoFaIsEnable(User user);

    void changePersonalInfo(Map<String,String> personalInfo,User user);

}
