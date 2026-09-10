package com.example.helloworld.customers.domain.exceptions;

public class StaleCustomerVersionException extends RuntimeException {

    public StaleCustomerVersionException(long expectedVersion, long currentVersion) {
        super("Customer version is stale. Expected " + expectedVersion
                + " but current version is " + currentVersion);
    }
}