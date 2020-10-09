package com.vsn.controllers;

import com.vsn.config.rest.model.MvcResponse;
import com.vsn.config.rest.model.MvcResponseError;
import com.vsn.dto.RegistrationUserDTO;
import com.vsn.exceptions.RegistrationValidDataException;
import com.vsn.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * REST controller for ROLE_USER requests.
 *
 * @author Maxim Turovets
 * @version 1.0
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class RegistrationController {
    private final UserService userService;

    @SneakyThrows
    @RequestMapping(value = "/registration", method = RequestMethod.POST, produces = "application/json")
    public MvcResponse registration(@RequestBody RegistrationUserDTO data, HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.register(data);
        } catch (RegistrationValidDataException e) {
            return new MvcResponseError(400, e.getMessage());
        } catch (IOException ex) {
            return new MvcResponseError(400, "Error created wallets");
        } catch (Exception ex) {
            return new MvcResponseError(400, "Error registration");
        }
        return new MvcResponse(200);
    }
}
