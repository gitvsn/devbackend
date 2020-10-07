package com.vsn.exceptions;

public class DomainInUseException extends Exception {
    public DomainInUseException() {
        super("Domain in use");
    }
}
