package com.vsn.controllers;

import com.vsn.config.rest.model.MvcResponse;
import com.vsn.config.rest.model.MvcResponseError;
import com.vsn.config.rest.model.MvcResponseObject;
import com.vsn.dto.SendDTO;
import com.vsn.entities.registration.User;
import com.vsn.entities.wallets.Wallet;
import com.vsn.exceptions.NotEnoughGas;
import com.vsn.exceptions.WrongBalanceException;
import com.vsn.securiry.jwt.JwtTokenProvider;
import com.vsn.services.interfaces.TransactionsService;
import com.vsn.services.interfaces.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final TransactionsService transactionsService;


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

    @RequestMapping(value = "/get_transactions_info", method = RequestMethod.POST, produces = "application/json")
    public MvcResponse getTransactionsInfo(HttpServletRequest request, HttpServletResponse response) {
        User user = jwtTokenProvider.getUser(request);
        try {
            return  new MvcResponseObject(200, transactionsService.getTransactionsAmount(user));
        } catch (Exception ex) {
            return new MvcResponseError(400, "Error get transactions");
        }
    }

    @RequestMapping(value = "/get_transactions_to_chart", method = RequestMethod.POST, produces = "application/json")
    public MvcResponse getTransactionsToChart(HttpServletRequest request, HttpServletResponse response) {
        User user = jwtTokenProvider.getUser(request);
        try {
            return  new MvcResponseObject(200, transactionsService.getTransactionsData(user));
        } catch (Exception ex) {
            return new MvcResponseError(400, "Error get transactions");
        }
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST, produces = "application/json")
    public MvcResponse send(@RequestBody SendDTO send , HttpServletRequest request) {
        User user = jwtTokenProvider.getUser(request);
        try {
             walletService.sendToAddress(send,user);
            return  new MvcResponseObject(200,true);
        } catch (WrongBalanceException e) {
            return new MvcResponseError(400, "Wrong  balance");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotEnoughGas | RuntimeException notEnoughGas) {
            return new MvcResponseError(400, notEnoughGas.getMessage() );
        }

        return  null;
    }


}