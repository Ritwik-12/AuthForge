package com.AuthForge.AuthForge.exception;

public class InvalidCredntialException extends RuntimeException{
    public InvalidCredntialException(){
        super("Invalid email or password");
    }
}
