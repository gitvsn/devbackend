package com.vsn.exceptions;

public class UserNotExistsException extends Exception {
    public UserNotExistsException() {
        super("The user does not exist!");
    }

    public UserNotExistsException(String msg) {
        super(msg);
    }
}
