package br.pucpr.system;

import br.pucpr.databases.DataLoader;

import java.util.*;

public class Carrinho {
    private Map<String, List<Object>> produtos;
    private double precoTotal;

    public Carrinho() {
        this.produtos = new HashMap<>();
        this.precoTotal = 0;
    }

    /**
     * Adiciona um novo produto ao carrinho do usuário.
     * Recebe nome e quantidade de um produto a ser inserido, e calcula o preço adicional sobre o preço total
     * no carrinho atual.
     * @param nome Nome do produto a ser inserido.
     * @param qtd Quantidade a ser inserida.
     */
    public void adicionarProduto(String nome, int qtd) {
        DataLoader loader = new DataLoader();
        var db = loader.loadDataToList(
                "src/br/pucpr/databases/products.csv", true);

        var p = db.stream()
                .filter(e -> e.get(0).equals(nome)).toList();

        if (!p.isEmpty()) {
            double preco = Double.parseDouble(p.get(0).get(1));
            List<Object> dados = new ArrayList<>();

            if (qtd > 0) {
                if (produtos.containsKey(nome)) {
                    dados.add(qtd + (Integer) produtos.get(nome).get(0));
                    dados.add(preco * ((Integer) produtos.get(nome).get(0) + qtd));
                    precoTotal += preco * ((Integer) produtos.get(nome).get(0) + qtd);
                } else {
                    dados.add(qtd);
                    dados.add(qtd * preco);
                    precoTotal += preco * qtd;
                }

                produtos.put(nome, dados);
            } else {
                throw new QuantInvalidaException("Quantidade inválida.");
            }
        } else throw new ProdutoNaoEncontrado("Produto não encontrado.");
    }


    /**
     * Permite a edição da quantidade de produtos no carrinho.
     * @param nome Nome do produto a ser editado.
     * @param nova_qtd Quantidade a ser inserida no novo estoque.
     */
    public void editarProduto(String nome, int nova_qtd) {
        DataLoader loader = new DataLoader();
        var db = loader.loadDataToList("src/br/pucpr/databases/products.csv", true);

        var p = db.stream()
                .filter(e -> e.get(0).equals(nome)).toList();

        var preco = Double.parseDouble(p.get(0).get(1));
        List<Object> dados = new ArrayList<>();
        if (nova_qtd > 0) {
            dados.add(nova_qtd);
            dados.add(preco * nova_qtd);
            produtos.replace(nome, produtos.get(nome), dados);

        } else throw new QuantInvalidaException("Quantidade inválida");
    }

    public void removerProduto(String nome) {
        precoTotal -= (Double) produtos.get(nome).get(1);
        produtos.remove(nome);
    }


    public double getPrecoTotal() {
        return precoTotal;
    }

    public Map<String, List<Object>> getProdutos() {
        return produtos;
    }

    public String produtosToString() {
        String str = "";
        for (String produto : produtos.keySet()) {
            str = str.concat(";" + produto);
        }
        return str;
    }

    @Override
    public String toString() {
        String str = "";
        for (String produto : produtos.keySet()) {
            str = str.concat("%d %s $%.2f;".formatted( (Integer) produtos.get(produto).get(0), produto,
                    (Double) produtos.get(produto).get(1)));
        }
        return str;
    }
}
