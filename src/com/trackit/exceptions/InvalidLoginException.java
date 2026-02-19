package com.trackit.exceptions;

public class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) {
        super(message);
    }
}