package com.AuthForge.AuthForge.exception;

public class InvalidRefreshTokenException extends RuntimeException{
   public InvalidRefreshTokenException(){
       super("Invalid Refresh Token");
   }
}
