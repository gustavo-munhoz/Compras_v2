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
 * Sistema de login com op��o de cadastrar usu�rio e usu�rio administrador OK
 * Menu de compras com:
 * Busca de produtos; OK
 * Listagem de produtos; OK
 * Adicionar produto ao carrinho; OK
 * Exibir carrinho / finalizar compra e salvar no hist�rico do cliente. OK
 * Relat�rio de clientes (fun��o dispon�vel s� para admin) com:
 * Listagem de clientes ordenados por gasto, n�mero de compras, total comprado e valor m�dio de compra; OK
 * Total geral vendido. OK
 * Listagens paginadas, com op��o de avan�ar e voltar OK
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
        System.out.println("Bem vindo ao novo hipermercado TXT!\nPor favor, fa�a login abaixo.");

        System.out.print("Digite seu CPF (somente n�meros): ");
        String login = in.nextLine();
        System.out.print("Digite sua senha: ");
        String senha = in.nextLine();
        user = new Usuario(login, senha);

        if (!user.existeUsuario()) {
            int tentativas = 0;
            System.out.println("Usu�rio inexsistente! Deseja se cadastrar?");
            System.out.println("[1] Sim\n[2] N�o");
            String cad = in.nextLine();

            while (!cad.equals("1") && !cad.equals("2")) {
                System.out.println("Insira uma op��o v�lida!");
                System.out.println("Usu�rio inexsistente! Deseja se cadastrar?");
                System.out.println("[1] Sim\n[2] N�o");
                System.out.print("Insira a op��o desejada: ");
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
                        System.out.println("At� a pr�xima!");
                        System.exit(0);
                    }
                } catch (CPFInvalidoException | UsuarioExistenteException e) {
                    tentativas++;
                    if (!user.existeUsuario()) System.out.println("CPF inv�lido! Tente novamente.");
                    else System.out.println("Usu�rio j� cadastrado. Tente novamente.");

                    System.out.print("Digite seu CPF (somente n�meros): ");
                    login = in.nextLine();
                    System.out.print("Digite sua senha: ");
                    senha = in.nextLine();
                    user = new Usuario(login, senha);

                    if (tentativas == 3) {
                        System.out.println("N�mero m�ximo de tentativas excedido. Finalizando...");
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
        System.out.println("[2] Trocar usu�rio");
        System.out.println("[3] Sobre");
        System.out.println("[4] Sair");
        System.out.print("Insira a op��o desejada: ");
    }

    static void mostrarMenuCompras() {
        System.out.println("\n---MENU DE COMPRAS---");
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Buscar produto");
        System.out.println("[2] Listar produtos");
        System.out.println("[3] Adicionar produto ao carrinho");
        System.out.println("[4] Exibir carrinho");
        System.out.println("[5] Voltar ao menu principal");
        System.out.print("Insira a op��o desejada: ");
    }

    static void mostrarMenuCarrinho() {
        System.out.println("------------------");
        System.out.println("[1] Continuar comprando");
        System.out.println("[2] Editar carrinho");
        System.out.println("[3] Finalizar compra");
        System.out.print("Insira a op��o desejada: ");
    }

    /**
     * Imprime um menu acess�vel somente ao usu�rio administrador.
     */
    static void mostrarMenuAdmin() {
        System.out.println("---ADMINISTRADOR---");
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Relat�rio de clientes");
        System.out.println("[2] Cadastrar produto");
        System.out.println("[3] Adicionar estoque");
        System.out.println("[4] Sair");
        System.out.print("Insira a op��o desejada: ");
    }
}

