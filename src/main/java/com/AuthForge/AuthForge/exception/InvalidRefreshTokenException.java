package com.AuthForge.AuthForge.exception;

public class InvalidRefreshTokenException extends RuntimeException{
   public InvalidRefreshTokenException(String message){
       super(message);
   }
}
