package br.com.filpo.frauddetector.domain.exceptions;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}