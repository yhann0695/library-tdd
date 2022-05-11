package br.com.library.api.exception;

public class BusinessException extends RuntimeException{

    public BusinessException(String isbnAlreadyRegistered) {
        super(isbnAlreadyRegistered);
    }
}
