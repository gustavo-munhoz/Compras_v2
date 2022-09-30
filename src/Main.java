import br.pucpr.databases.DataLoader;
import br.pucpr.system.CPFInvalidoException;
import br.pucpr.system.Usuario;
import br.pucpr.system.UsuarioExistenteException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Requisitos do programa:
 * Sistema de login com opção de cadastrar usuário e usuário administrador OK
 * Menu de compras com:
 * Busca de produtos; OK
 * Listagem de produtos; OK
 * Adicionar produto ao carrinho; OK
 * Exibir carrinho / finalizar compra e salvar no histórico do cliente. OK
 * Relatório de clientes (função disponível só para admin) com:
 * Listagem de clientes ordenados por gasto, número de compras, total comprado e valor médio de compra; OK
 * Total geral vendido. OK
 * Listagens paginadas, com opção de avançar e voltar OK
 */

public class Main {
    public static void main(String[] args) throws InterruptedException {
        DataLoader loader = new DataLoader();

        List<List<String>> produtos = loader.loadDataToList(
                "src/br/pucpr/databases/products.csv",
                true);
        List<List<String>> usuarios = loader.loadDataToList(
                "src/br/pucpr/databases/users.csv",
                true);

        Usuario user;

        int escolha_menu, escolha_compras, escolha_carrinho, escolha_editar, escolha_admin;
        Scanner in = new Scanner(System.in);
        System.out.println("Bem vindo ao novo hipermercado TXT!\nPor favor, faça login abaixo.");

        System.out.print("Digite seu CPF (somente números): ");
        String login = in.nextLine();
        System.out.print("Digite sua senha: ");
        String senha = in.nextLine();
        user = new Usuario(login, senha);

        if (!user.existeUsuario()) {
            int tentativas = 0;
            System.out.println("Usuário inexsistente! Deseja se cadastrar?");
            System.out.println("[1] Sim\n[2] Não");
            String cad = in.nextLine();

            while (!cad.equals("1") && !cad.equals("2")) {
                System.out.println("Insira uma opção válida!");
                System.out.println("Usuário inexsistente! Deseja se cadastrar?");
                System.out.println("[1] Sim\n[2] Não");
                System.out.print("Insira a opção desejada: ");
                cad = in.nextLine();
            }

            do {
                try {
                    if (cad.equals("1")) {
                        user.cadastrarUsuario();
                        cad = "0";
                    } else {
                        System.out.println("Finalizando...");
                        Thread.sleep(500);
                        System.out.println("Até a próxima!");
                        System.exit(0);
                    }
                } catch (CPFInvalidoException | UsuarioExistenteException e) {
                    tentativas++;
                    if (!user.existeUsuario()) System.out.println("CPF inválido! Tente novamente.");
                    else System.out.println("Usuário já cadastrado. Tente novamente.");

                    System.out.print("Digite seu CPF (somente números): ");
                    login = in.nextLine();
                    System.out.print("Digite sua senha: ");
                    senha = in.nextLine();
                    user = new Usuario(login, senha);

                    if (tentativas == 3) {
                        System.out.println("Número máximo de tentativas excedido. Finalizando...");
                        System.exit(1);
                    }
                }
            } while (!cad.equals("0"));
        }

        else if (user.senhaIncorreta()) {
            do {
                System.out.println("Senha incorreta! Tente novamente.");
                System.out.print("Insira sua senha: ");
                senha = in.nextLine();
                user = new Usuario(login, senha);
            } while (user.senhaIncorreta());
        }
        System.out.println("\nSeja bem vindo!");

        if (!user.getLogin().equals("admin"))
            mostrarMenuPrincipal();

    }

    static void mostrarMenuPrincipal() {
        System.out.println("\n---MENU PRINCIPAL---");
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Fazer compras");
        System.out.println("[2] Trocar usuário");
        System.out.println("[3] Sobre");
        System.out.println("[4] Sair");
        System.out.print("Insira a opção desejada: ");
    }

    static void mostrarMenuCompras() {
        System.out.println("\n---MENU DE COMPRAS---");
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Buscar produto");
        System.out.println("[2] Listar produtos");
        System.out.println("[3] Adicionar produto ao carrinho");
        System.out.println("[4] Exibir carrinho");
        System.out.println("[5] Voltar ao menu principal");
        System.out.print("Insira a opção desejada: ");
    }

    static void mostrarMenuCarrinho() {
        System.out.println("------------------");
        System.out.println("[1] Continuar comprando");
        System.out.println("[2] Editar carrinho");
        System.out.println("[3] Finalizar compra");
        System.out.print("Insira a opção desejada: ");
    }

    /**
     * Imprime um menu acessível somente ao usuário administrador.
     */
    static void mostrarMenuAdmin() {
        System.out.println("---ADMINISTRADOR---");
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Relatório de clientes");
        System.out.println("[2] Cadastrar produto");
        System.out.println("[3] Adicionar estoque");
        System.out.println("[4] Sair");
        System.out.print("Insira a opção desejada: ");
    }
}

