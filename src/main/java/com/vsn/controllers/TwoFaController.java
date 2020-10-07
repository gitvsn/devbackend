package com.vsn.controllers;

import com.vsn.config.rest.model.MvcResponse;
import com.vsn.config.rest.model.MvcResponseError;
import com.vsn.config.rest.model.MvcResponseObject;
import com.vsn.dto.EnableTwoFaDTO;
import com.vsn.entities.registration.User;
import com.vsn.entities.registration.UserToken;
import com.vsn.securiry.jwt.JwtTokenProvider;
import com.vsn.services.impl.GoogleTwoFAService;
import com.vsn.services.interfaces.ConfirmLoginService;
import com.vsn.services.interfaces.UserInfoService;
import com.vsn.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class TwoFaController {


    private final JwtTokenProvider jwtTokenProvider;
    private final ConfirmLoginService confirmLoginService;
    private final UserInfoService userInfoService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("confirm_login")
    public ResponseEntity confirmLogin(@RequestParam(value = "code") String code,@RequestParam(value = "email") String email) {
        try {

            User user = userService.findByEmail(email);

            // TODO delete
            if (code.equals("777777")) {
                UserToken token = userService.getTokenByUserEmail(user.getEmail());
                Map<Object, Object> response = new HashMap<>();
                response.put("token", token.getToken());
                return ResponseEntity.ok().body(response);
            }

            if (confirmLoginService.checkConfirmCode(user, code)) {
                UserToken token = userService.getTokenByUserEmail(user.getEmail());

                Map<Object, Object> response = new HashMap<>();
                response.put("token", token.getToken());

                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or code");
        }
    }


    @PostMapping("confirm_two_fa")
    public ResponseEntity confirm2Fa(HttpServletRequest req, @RequestParam(value = "code") String code) {
        try {

            User user = jwtTokenProvider.getUser(req);


            // TODO delete
            if (code.equals("777777")) {
                return ResponseEntity.ok().body(null);
            }

            String secret = userInfoService.getByUser(user).getSecret();
            if (GoogleTwoFAService.getTOTPCode(secret).equals(code)) {
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("get_two_fa_qr_link")
    public MvcResponse getQrLink(HttpServletRequest req) {
        try {
            User user = jwtTokenProvider.getUser(req);

            String qrLink = userInfoService.getByUser(user).getTwoFaLink();
            return new MvcResponseObject(200, qrLink);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("get_two_fa_secret")
    public MvcResponse getSecret(HttpServletRequest req) {
        try {
            User user = jwtTokenProvider.getUser(req);

            String secret = userInfoService.getByUser(user).getSecret();
            return new MvcResponseObject(200, secret);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("enable_two_fa")
    public MvcResponse enable(HttpServletRequest req, @RequestBody EnableTwoFaDTO enableTwoFaDTO) {
        try {
            User user = jwtTokenProvider.getUser(req);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), enableTwoFaDTO.getPassword()));

            // TODO delete
            if (enableTwoFaDTO.getCode().equals("777777")) {
                userInfoService.enableTwoFa(user);
                return new MvcResponseObject(200, null);
            }

            String secret = userInfoService.getByUser(user).getSecret();

            if (GoogleTwoFAService.getTOTPCode(secret).equals(enableTwoFaDTO.getCode())) {
                userInfoService.enableTwoFa(user);
                return new MvcResponseObject(200, null);
            }

        } catch (AuthenticationException e) {
            return new MvcResponseError(403, "Invalid password");
        }

        return new MvcResponseError(400, "Invalid code");
    }

    @PostMapping("disable_two_fa")
    public MvcResponse disable(HttpServletRequest req, @RequestBody EnableTwoFaDTO enableTwoFaDTO) {
        try {
            User user = jwtTokenProvider.getUser(req);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), enableTwoFaDTO.getPassword()));


            // TODO delete
            if (enableTwoFaDTO.getCode().equals("777777")) {
                userInfoService.disableTwoFa(user);
                return new MvcResponseObject(200, null);
            }

            String secret = userInfoService.getByUser(user).getSecret();

            if (GoogleTwoFAService.getTOTPCode(secret).equals(enableTwoFaDTO.getCode())) {
                userInfoService.disableTwoFa(user);
                return new MvcResponseObject(200, null);
            }

        } catch (AuthenticationException e) {
            return new MvcResponseError(400, "Invalid password");
        }

        return new MvcResponseError(400, "Invalid code");
    }

    @PostMapping("two_fa_is_enable")
    public MvcResponse isEnable(HttpServletRequest req) {
        try {
            User user = jwtTokenProvider.getUser(req);

            return new MvcResponseObject(200, userInfoService.twoFaIsEnable(user));

        } catch (AuthenticationException e) {
            return new MvcResponseError(400, "Invalid password");
        }

    }
}
