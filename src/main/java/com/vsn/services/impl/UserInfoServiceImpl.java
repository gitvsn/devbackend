package com.vsn.services.impl;

import com.vsn.entities.registration.Avatar;
import com.vsn.entities.registration.User;
import com.vsn.entities.registration.UserInfo;
import com.vsn.repositories.AvatarRepository;
import com.vsn.repositories.UserInfoRepository;
import com.vsn.services.interfaces.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final AvatarRepository avatarRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    @Transactional
    public void saveAvatar(Avatar avatar) {
        if (loadAvatar(avatar.getUserId()).isPresent()) {
            removeAvatar(avatar.getUserId());
        }
        avatarRepository.save(avatar);
    }

    @Override
    public Optional<Avatar> loadAvatar(User user) {
        return avatarRepository.getAvatarByUserId(user.getId());
    }

    private Optional<Avatar> loadAvatar(Long userId) {
        return avatarRepository.getAvatarByUserId(userId);
    }

    @Override
    public void removeAvatar(Long userId) {
        avatarRepository.delete(loadAvatar(userId).get());
    }

    @Override
    @SneakyThrows
    public byte[] convertImageToByteArray(MultipartFile multipartFile) {
        BufferedImage image = ImageIO.read(multipartFile.getInputStream());
        int width = image.getWidth();
        int height = image.getHeight();

        while (width > 300 || height > 300) {
            width /= 2;
            height /= 2;
        }
        BufferedImage resizedImage = Thumbnails.of(multipartFile.getInputStream())
                .forceSize(width, height)
                .asBufferedImage();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", baos);
        return baos.toByteArray();
    }

    @Override
    public UserInfo saveUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo getByUser(User user) {
        return userInfoRepository.getByUserId(user.getId()).orElse(null);
    }

    @Override
    public void enableTwoFa(User user) {
        UserInfo userInfo = getByUser(user);
        userInfo.setTwoFaEnable(true);
        saveUserInfo(userInfo);
    }

    @Override
    public void disableTwoFa(User user) {
        UserInfo userInfo = getByUser(user);
        userInfo.setTwoFaEnable(false);
        saveUserInfo(userInfo);
    }

    @Override
    public boolean twoFaIsEnable(User user) {
        return getByUser(user).isTwoFaEnable();
    }

    @Override
    public void changePersonalInfo(Map<String, String> personalInfo, User user) {
        UserInfo userInfo = getByUser(user);

        String name = personalInfo.get("name");
        if (validNames(name)) {
            userInfo.setName(name);
        }

        String surName = personalInfo.get("surname");
        if (validNames(surName)) {
            userInfo.setSurname(surName);
        }

        String country = personalInfo.get("country");
        if (validNames(country)) {
            userInfo.setCountry(country);
        }

        String phone = personalInfo.get("phone");
        if (validNames(phone)) {
            userInfo.setPhone(phone);
        }

        String email = personalInfo.get("email");
        if (EmailValidator.getInstance().isValid(email)) {
            userInfo.setEmail(email);
        }

        saveUserInfo(userInfo);
    }

    private boolean validNames(String name) {
        return (name != null && !name.equals(""));
    }

}
