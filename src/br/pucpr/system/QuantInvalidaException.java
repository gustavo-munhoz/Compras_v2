package br.pucpr.system;

public class QuantInvalidaException extends RuntimeException {
    public QuantInvalidaException() {
        super();
    }
    public QuantInvalidaException(String message) {
        super(message);
    }
}
