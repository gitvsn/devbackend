package com.vsn.controllers;

import com.vsn.config.rest.model.MvcResponse;
import com.vsn.config.rest.model.MvcResponseError;
import com.vsn.config.rest.model.MvcResponseObject;
import com.vsn.entities.registration.User;
import com.vsn.entities.wallets.Wallet;
import com.vsn.securiry.jwt.JwtTokenProvider;
import com.vsn.services.interfaces.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;

/**
 * REST controller for ROLE_USER requests.
 *
 * @author Maxim Turovets
 * @version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class WalletController {

    private final WalletService walletService;
    private final JwtTokenProvider jwtTokenProvider;


    @RequestMapping(value = "/getWallets", method = RequestMethod.POST, produces = "application/json")
    public MvcResponse getWallets(HttpServletRequest request, HttpServletResponse response) {
        User user = jwtTokenProvider.getUser(request);
        try {
        List<Wallet> walletList = walletService.getWalletsByUser(user);
        walletList.sort(Comparator.comparing(Wallet::getCurrency));

           return  new MvcResponseObject(200,walletList);
        } catch (Exception ex) {
            return new MvcResponseError(400, "Error get wallets");
        }
    }


}