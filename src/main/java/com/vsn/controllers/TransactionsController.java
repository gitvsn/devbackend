package com.vsn.controllers;


import com.vsn.config.rest.model.MvcResponse;
import com.vsn.config.rest.model.MvcResponseError;
import com.vsn.config.rest.model.MvcResponseObject;
import com.vsn.entities.registration.User;
import com.vsn.securiry.jwt.JwtTokenProvider;
import com.vsn.services.interfaces.ConfirmLoginService;
import com.vsn.services.interfaces.TransactionsService;
import com.vsn.services.interfaces.UserInfoService;
import com.vsn.services.interfaces.UserService;
import com.vsn.utils.page.InnerPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *  Author Maxim Turovets
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class TransactionsController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TransactionsService transactionsService;

    @RequestMapping(value = "/get_user_transactions", method = RequestMethod.POST, produces = "application/json")
    public MvcResponse getUserTransactions(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                           HttpServletRequest request) {
        User user = jwtTokenProvider.getUser(request);
        List transList = transactionsService.getByUser(user);

        InnerPage innerPage = new InnerPage(transList);

        PageImpl s = innerPage.getPageInObjectList(page,size);

        try {
          //  return  new MvcResponseObject(200,innerPage.getPageInObjectList(page,size));
            return  new MvcResponseObject(200,transList);
        } catch (Exception ex) {
            return new MvcResponseError(400, "Error");
        }
    }
}
