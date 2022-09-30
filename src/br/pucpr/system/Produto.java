package br.pucpr.system;

public class Produto {
    private final String nome;
    private double preco;
    private int estoque;

    public Produto(String nome, double preco, int estoque) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public String getNome() {
        return nome;
    }

    public int getEstoque() {
        return estoque;
    }

    public double getPreco() {
        return preco;
    }

    @Override
    public String toString() {
        return "%s;%d;%.2f".formatted(nome,estoque,preco);
    }

}