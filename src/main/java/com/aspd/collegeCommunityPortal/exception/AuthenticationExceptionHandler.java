package com.aspd.collegeCommunityPortal.exception;

import com.aspd.collegeCommunityPortal.beans.response.HttpResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;

@RestControllerAdvice
public class AuthenticationExceptionHandler implements ErrorController {
    private static final String ACCOUNT_LOCKED="Your account has been locked. Please contact administration";
    private static final String INCORRECT_CREDENTIALS="Username / Password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED="Your account has been disabled. If this is an error, please contact administration";
    private static final String NOT_ENOUGH_PERMISSION="You do not have enough permission";
    public static final String ERROR_PATH="/error";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException(){
        return createHttResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException(){
        return createHttResponse(HttpStatus.BAD_REQUEST,INCORRECT_CREDENTIALS);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(){
        return createHttResponse(HttpStatus.FORBIDDEN,NOT_ENOUGH_PERMISSION);
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException(){
        return createHttResponse(HttpStatus.UNAUTHORIZED,ACCOUNT_LOCKED);
    }
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<HttpResponse> signatureException(){
        return createHttResponse(HttpStatus.UNAUTHORIZED,"JWT signature does not match");
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<HttpResponse> expiredJwtException(){
        return createHttResponse(HttpStatus.UNAUTHORIZED,"JWT Token expired");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> illegalStateException(Exception exception){
        return createHttResponse(HttpStatus.INTERNAL_SERVER_ERROR,exception.getMessage());
    }
    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> notFound404(){
        return createHttResponse(HttpStatus.NOT_FOUND,"There is no mapping for this URL");
    }
    public String getErrorPath() {
        return ERROR_PATH;
    }

    private ResponseEntity<HttpResponse> createHttResponse(HttpStatus httpStatus,String message){
        HttpResponse httpResponse=new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase(),message.toUpperCase());
        return new ResponseEntity<>(httpResponse,httpStatus);
    }


}
