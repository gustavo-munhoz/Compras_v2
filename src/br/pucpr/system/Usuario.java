package br.pucpr.system;

import br.pucpr.databases.DataLoader;

import java.io.*;
import java.util.Arrays;


public class Usuario {
    private final String login;
    private final String senha;
    private Carrinho carrinho;
    private Double gasto;


    public Usuario(String login, String senha) {
        DataLoader loader = new DataLoader();

        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Campo <usuario> vazio.");
        }
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Campo <senha> vazio.");
        }

        var db = loader.loadDataToList(
                "src/br/pucpr/databases/users.csv",
                true);

        var u = db.stream()
                .filter(e -> e.get(0).equals(login)).toList();

        if (!u.isEmpty()) {
            var strGasto = u.get(0).get(2);

            this.gasto = Double.parseDouble(
                    strGasto.substring(strGasto.lastIndexOf("|") + 1,
                            strGasto.length() - 1));
        } else this.gasto = 0.0;

        this.login = login;
        this.senha = senha;
        this.carrinho = new Carrinho();
    }


    public Carrinho getCarrinho() {
        return carrinho;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public Double getGasto() {
        return gasto;
    }

    public void somarGasto(Double valor) {
        gasto += valor;
    }

    /**
     * Procura no usuario no banco de dados users.csv, mas nao verifica se a senha est� correta.
     * @return true caso o usu�rio exista, false caso contr�rio.
     */
    public boolean existeUsuario() {
        DataLoader loader = new DataLoader();
        var db = loader.loadDataToList("src/br/pucpr/databases/users.csv", true);

        for (var user : db) {
            if (user.get(0).equals(login)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se a senha inserida est� incorreta. Procura o valor inserido dentro do banco de dados,
     * e confere se corresponde � senha do usuario inserido.
     * @return true caso senha esteja incorreta, false caso contr�rio.
     */
    public boolean senhaIncorreta() {
        DataLoader loader = new DataLoader();
        var db = loader.loadDataToList("src/br/pucpr/databases/users.csv", true);

        for (var user : db) {
            if (user.get(0).equals(login)) {
                if (user.get(1).equals(senha)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Insere o usu�rio no banco de dados, com login, senha e total de gastos com valor inicial 0.
     */
    public void cadastrarUsuario() {
        try (FileWriter fw = new FileWriter("src/br/pucpr/databases/users.csv", true)) {
            DataLoader loader = new DataLoader();
            var db = loader.loadDataToList("src/br/pucpr/databases/users.csv", true);

            if (validarCPF(login)) {
                if (!existeUsuario()) {
                    var user = Arrays.asList(login, senha, "{|0.0}");
                    db.add(user);
                    fw.write("\n" + login + ";" + senha + ";" + "{|0.0}");
                }
                else throw new UsuarioExistenteException("Usu�rio j� cadastrado.");
            }
            else throw new CPFInvalidoException("CPF inv�lido.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validarCPF(String cpf) {
        int soma = 0, fator = 10;

        if (cpf.length() == 11) {
            for (char c : cpf.substring(0, 9).toCharArray()) {
                String s = String.valueOf(c);
                soma += fator * Integer.parseInt(s);
                fator -= 1;
            }
            if (soma % 11 < 2) {
                if (!String.valueOf(cpf.charAt(9)).equals("0") && !String.valueOf(cpf.charAt(9)).equals("1")) {
                    return false;
                }

            } else {
                if (!String.valueOf(cpf.charAt(9)).equals("%d".formatted(11 - soma % 11))) {
                    return false;
                }
            }
            soma = 0;
            fator = 11;

            for (char c : cpf.substring(0, 10).toCharArray()) {
                String s = String.valueOf(c);
                soma += fator * Integer.parseInt(s);
                fator -= 1;
            }

            if (soma % 11 < 2) {
                return String.valueOf(cpf.charAt(10)).equals("0") || String.valueOf(cpf.charAt(10)).equals("1");

            } else {
                return String.valueOf(cpf.charAt(10)).equals("%d".formatted(11 - soma % 11));
            }
        } else {
            return false;
        }
    }


    /**
     * Converte os objetos do tipo Object do atributo produtos para Integer e Double,
     * mostrando assim a quantidade, nome e pre�o de cada produto contido no carrinho.
     * Mostra tamb�m o pre�o total.
     */
    public void exibirCarrinho() {
        System.out.println("\n---MEU CARRINHO---");
        var produtos = carrinho.getProdutos();
        for (String produto : produtos.keySet()) {
            System.out.printf("%d %s, $%.2f\n", (Integer) produtos.get(produto).get(0),
                    produto, (Double) produtos.get(produto).get(1));
        }
        System.out.printf("Pre�o total: $%.2f\n", carrinho.getPrecoTotal());
    }
}
