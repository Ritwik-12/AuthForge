package com.AuthForge.AuthForge.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String email){
        super("Email already Registered: "+email);
    }
}
