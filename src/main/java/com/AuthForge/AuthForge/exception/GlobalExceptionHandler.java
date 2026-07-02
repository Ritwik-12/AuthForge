package com.AuthForge.AuthForge.exception;

import com.AuthForge.AuthForge.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409,"CONFLICT",ex.getMessage(), LocalDateTime.now()
                ));

    }

    @ExceptionHandler(InvalidCredntialException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredential(InvalidCredntialException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401,"UNAUTHORIZED",ex.getMessage(),LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex){
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                  .body(new ErrorResponse(401,"UNAUTHOIZED",ex.getMessage(),LocalDateTime.now()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401,"UNAUTHORIZED",ex.getMessage(),LocalDateTime.now()));
    }

    @ExceptionHandler (JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401,"UNAUTHORIZED",ex.getMessage(),LocalDateTime.now()));
    }

    //validation exception //@Valid failure
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex){

        Map<String,String> error=ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe->fe.getDefaultMessage() !=null
                                ?fe.getDefaultMessage():"Invalid value"

                ));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(error);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex){
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500,"INTERNAL_SERVER_ERROR"
                        ,"An Unexpected Error Occured",LocalDateTime.now()));
    }


}
