package br.pucpr.system;

public class CPFInvalidoException extends IllegalArgumentException {

    public CPFInvalidoException(String message) {
        super(message);
    }
}
