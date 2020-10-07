package com.vsn.controllers;

import com.vsn.config.rest.model.MvcResponse;
import com.vsn.config.rest.model.MvcResponseError;
import com.vsn.config.rest.model.MvcResponseObject;
import com.vsn.dto.AuthorizationDTO;
import com.vsn.entities.confirm.ConfirmLogin;
import com.vsn.entities.registration.User;
import com.vsn.entities.registration.UserInfo;
import com.vsn.exceptions.RegistrationValidDataException;
import com.vsn.securiry.jwt.JwtTokenProvider;
import com.vsn.services.interfaces.ConfirmLoginService;
import com.vsn.services.interfaces.UserInfoService;
import com.vsn.services.interfaces.UserService;
import com.vsn.utils.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api")
public class AuthenticationController {

//    @Value("${confirm.login}")
//    private Boolean confirmLogin;
    @Autowired
    private String clientAddress;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserInfoService userInfoService;
    private final EmailSender emailSender;
    private final ConfirmLoginService confirmLoginService;


    @PostMapping("authorization")
    public MvcResponse login(@RequestBody AuthorizationDTO authorization) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authorization.getEmail(), authorization.getPassword()));
            User user = userService.findByEmail(authorization.getEmail());

            if (user == null) {
                throw new UsernameNotFoundException("User with email: " + authorization.getEmail() + " not found");
            }

            String token = jwtTokenProvider.createToken(authorization.getEmail());

            userService.saveUserToken(user,token);

            Map<Object, Object> response = new HashMap<>();
            response.put("username", authorization.getEmail());

            if (userInfoService.twoFaIsEnable(user)) {
                ConfirmLogin confirmLogin = confirmLoginService.createConfirmLogin(user);
                emailSender.sendEmailOnLoginConfirm(user.getEmail(), confirmLogin.getCode());
            } else {
                response.put("token", token);
            }

            return new MvcResponseObject(200, response);
        } catch (AuthenticationException e) {
            return new MvcResponseError(400, "Error authorization");
        }
    }


    @PostMapping("check_token")
    public ResponseEntity test(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(null);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }


    @PermitAll
    @PostMapping("restore_password/email/{email}")
    public ResponseEntity restorePasswordCheckEmail(@PathVariable("email") String email, HttpServletRequest req) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User with email: " + email + " not found");
            }

            log.info("Start restore password...");
            log.info("User : {}",email);

            ConfirmLogin confirmRestorePassword = confirmLoginService.createConfirmRestorePassword(user);
            String link = String.format("%s/new?type=r&token=%s",clientAddress,confirmRestorePassword.getCode());

            emailSender.sendEmailOnRestorePasswordConfirm(user.getEmail(),link);

            log.info("Send link : {}",link);

            return ResponseEntity.ok(null);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email");
        }
    }


    @PermitAll
    @PostMapping("restore_password/{token}/{password}")
    public ResponseEntity restorePasswordCheckToken(@PathVariable("token") String token,@PathVariable("password") String password, HttpServletRequest req) {
        try {
            User user = userService.findById(confirmLoginService.getUserIdByCode(token));

            if (user == null) {
                throw new UsernameNotFoundException("");
            }

            if(confirmLoginService.checkConfirmCode(user,token)){
                userService.changePassword(user,password);
                log.info("Success change password user {}",user.getEmail());
            }

            return ResponseEntity.ok(null);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid token");
        } catch (RegistrationValidDataException e) {
            throw new BadCredentialsException("Invalid password");
        }
    }



}
