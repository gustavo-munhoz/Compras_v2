package br.pucpr.system;

import java.util.ArrayList;
import java.util.List;

public class Pagina {
    private List<String> texto;
    private int linha_inicial;
    private int linha_final;


    public Pagina(int linha_inicial, int linha_final) {
        this.linha_inicial = linha_inicial;
        this.linha_final = linha_final;
        this.texto = new ArrayList<>();
    }

    public List<String> getTexto() {
        return texto;
    }

    public int getLinha_inicial() {
        return linha_inicial;
    }

    public int getLinha_final() {
        return linha_final;
    }

    public void setLinha_inicial(int linha_inicial) {
        this.linha_inicial = linha_inicial;
    }

    public void setLinha_final(int linha_final) {
        this.linha_final = linha_final;
    }

    /**
     * Adiciona uma string à lista de textos.
     *
     * @param str é o texto a ser inserido.
     */
    public void adicionarTexto(String str) {
        this.texto.add(str);
    }

    /**
     * Permite a paginação do texto recebido no atributo da classe.
     *
     * @return uma lista delimitada pelos índices escolhidos (linha final e inicial).
     */
    public List<String> mostrarPagina() {
        if (linha_final <= texto.size()) {
            return texto.subList(linha_inicial, linha_final);
        } else {
            return texto.subList(linha_inicial, texto.size());
        }
    }

    /**
     * Transforma a linha inicial na final e avança a final pelo tamanho da página.
     */
    public void avancarPagina() {
        int qtd_linhas = linha_final - linha_inicial;
        linha_inicial = linha_final;
        linha_final += qtd_linhas;
    }

    /**
     * Transforma a linha final em inicial e retrai a inicial pelo tamanho da página.
     */
    public void voltarPagina() {
        int qtd_linhas = linha_final - linha_inicial;
        linha_final = linha_inicial;
        linha_inicial -= qtd_linhas;
    }
}