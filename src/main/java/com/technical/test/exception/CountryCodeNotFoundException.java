package com.technical.test.exception;

public class CountryCodeNotFoundException extends Exception {
    public CountryCodeNotFoundException() {
    }

    public CountryCodeNotFoundException(String message) {
        super(message);
    }
}
