package com.vsn.controllers;

import com.vsn.config.rest.model.MvcResponse;
import com.vsn.config.rest.model.MvcResponseError;
import com.vsn.config.rest.model.MvcResponseObject;
import com.vsn.entities.registration.Avatar;
import com.vsn.entities.registration.User;
import com.vsn.entities.registration.UserInfo;
import com.vsn.exceptions.RegistrationValidDataException;
import com.vsn.response_entity.AvatarRequest;
import com.vsn.securiry.jwt.JwtTokenProvider;
import com.vsn.services.interfaces.UserInfoService;
import com.vsn.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * REST controller for ROLE_USER requests.
 *
 * @author Maxim Turovets
 * @version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class UserInfoController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserInfoService userInfoService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @RequestMapping(value = "/get_id", method = RequestMethod.POST, produces = "application/json")
    public MvcResponse getUserId(HttpServletRequest request) {
        User user = jwtTokenProvider.getUser(request);
        try {
            return new MvcResponseObject(200, user.getId());
        } catch (Exception ex) {
            return new MvcResponseError(400, "Error");
        }
    }

    @RequestMapping(value = "/upload/avatar", method = RequestMethod.POST, produces = "application/json")
    public MvcResponse uploadAvatar(AvatarRequest avatarRequest, HttpServletRequest request) {

        try {
            userInfoService.saveAvatar(mapToAvatar(avatarRequest, request));
            return new MvcResponseObject(200, null);
        } catch (Exception ex) {
            return new MvcResponseError(400, "Error saved");
        }
    }

    @RequestMapping(value = "/getAvatar", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Avatar> getAvatar(HttpServletRequest request) {
        Avatar avatar = userInfoService.loadAvatar(jwtTokenProvider.getUser(request)).orElse(null);
        return new ResponseEntity<>(avatar, HttpStatus.OK);
    }

    @RequestMapping(value = "/change_personal_info", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Avatar> changeEmail(@RequestBody Map<String, String> personalInfo, HttpServletRequest request) {

        String email = personalInfo.get("email");
        User user = jwtTokenProvider.getUser(request);

        userService.changeEmail(user, email);
        userInfoService.changePersonalInfo(personalInfo, user);

        String oldPass = personalInfo.get("oldPassword");
        String newPass = personalInfo.get("newPassword");


        try {
            if(oldPass != null  && !newPass.isEmpty()){
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), oldPass));
                userService.changePassword(user,newPass);
            }
        } catch (RuntimeException | RegistrationValidDataException ex){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/get_personal_info", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> getPersonalInfo(HttpServletRequest request) {

        User user = jwtTokenProvider.getUser(request);
        UserInfo userInfo = userInfoService.getByUser(user);

        Map<Object, Object> response = new HashMap<>();

        response.put("name", userInfo.getName());
        response.put("surname", userInfo.getSurname());
        response.put("country", userInfo.getCountry());
        response.put("phone", userInfo.getPhone());
        response.put("email", userInfo.getEmail());



        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private Avatar mapToAvatar(AvatarRequest avatarRequest, HttpServletRequest request) {
        return new Avatar(
                jwtTokenProvider.getUser(request).getId(),
                userInfoService.convertImageToByteArray(avatarRequest.getDocument())
        );
    }

}