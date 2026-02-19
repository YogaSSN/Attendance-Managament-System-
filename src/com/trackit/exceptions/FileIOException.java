package com.trackit.exceptions;

public class FileIOException extends Exception {
    public FileIOException(String message) {
        super(message);
    }
    
    public FileIOException(String message, Throwable cause) {
        super(message, cause);
    }
}