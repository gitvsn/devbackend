package com.vsn.exceptions;


public class WrongBalanceException extends Exception {
    public WrongBalanceException() {
        super("Not enough funds in the account");
    }

    public WrongBalanceException(String msg) {
        super(msg);
    }
}