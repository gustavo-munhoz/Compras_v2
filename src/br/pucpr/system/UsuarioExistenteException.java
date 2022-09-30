package br.pucpr.system;

public class UsuarioExistenteException extends RuntimeException {
    public UsuarioExistenteException(String message) {
        super(message);
    }
}
