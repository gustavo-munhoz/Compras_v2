package br.pucpr.system;

public class ProdutoNaoEncontrado extends RuntimeException {
    public ProdutoNaoEncontrado(String message) {
        super(message);
    }
}
