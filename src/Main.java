import br.pucpr.databases.DataLoader;
import br.pucpr.system.*;


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
        boolean sair = false;

        List<List<String>> produtos = loader.loadDataToList(
                "src/br/pucpr/databases/products.csv",
                true);
        List<List<String>> usuarios = loader.loadDataToList(
                "src/br/pucpr/databases/users.csv",
                true);

        Usuario user = null;

        int escolha_menu, escolha_compras, escolha_carrinho, escolha_editar, escolha_admin;
        Scanner in = new Scanner(System.in);

        do {
            if (user == null) {
                System.out.println("Bem vindo ao novo e refatorado supermercado TXT!\nPor favor, fa�a login abaixo.");

                System.out.print("Digite seu CPF (somente n�meros): ");
                String login = in.nextLine();
                System.out.print("Digite sua senha: ");
                String senha = in.nextLine();
                user = new Usuario(login, senha);

                if (!user.existeUsuario()) {
                    int tentativas = 0;
                    System.out.println("Usu�rio inexsistente! Deseja se cadastrar?");
                    System.out.println("[1] Sim\n[2] N�o");
                    System.out.print("Insira a op��o desejada: ");
                    String tentativa = in.nextLine();

                    while (!tentativa.equals("1") && !tentativa.equals("2")) {
                        System.out.println("Insira uma op��o v�lida!");
                        System.out.println("Usu�rio inexsistente! Deseja se cadastrar?");
                        System.out.println("[1] Sim\n[2] N�o");
                        System.out.print("Insira a op��o desejada: ");
                        tentativa = in.nextLine();
                    }

                    do {
                        try {
                            if (tentativa.equals("1")) {
                                user.cadastrarUsuario();
                                tentativa = "0";
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
                    } while (!tentativa.equals("0"));
                } else if (user.senhaIncorreta()) {
                    do {
                        System.out.println("Senha incorreta! Tente novamente.");
                        System.out.print("Insira sua senha: ");
                        senha = in.nextLine();
                        user = new Usuario(login, senha);
                    } while (user.senhaIncorreta());
                }
                System.out.println("\nSeja bem vindo!");

            } else {
                if (!user.getLogin().equals("admin")) {
                    mostrarMenuPrincipal();
                    escolha_menu = Integer.parseInt(in.nextLine());

                    switch (escolha_menu) {
                        case 1 -> {
                            do {
                                mostrarMenuCompras();
                                escolha_compras = Integer.parseInt(in.nextLine());
                                switch (escolha_compras) {
                                    case 1 -> {
                                        System.out.print("Insira o nome do produto a buscar: ");
                                        String busca = in.nextLine().toUpperCase();
                                        var resultado = produtos.stream()
                                                .filter(e -> e.get(0).contains(busca)).toList();

                                        if (resultado.isEmpty()) {
                                            System.out.println("Nenhum resultado encontrado.");
                                            standby();

                                        } else {
                                            System.out.println("RESULTADOS: ");
                                            resultado.forEach(e -> System.out.printf("%s...........R$%s%n%n",
                                                            e.get(0), e.get(1)));
                                            standby();
                                        }
                                    }
                                    case 2 -> {
                                        Pagina pag = new Pagina(0, 6);
                                        produtos.forEach(e -> pag.adicionarTexto("%s...........R$%s%n"
                                                                        .formatted(e.get(0), e.get(1))));
                                        int continuar;
                                        int indice = 1;

                                        do {
                                            System.out.printf("\n\t   --P�GINA %d--\n", indice);
                                            for (String l : pag.mostrarPagina()) {
                                                System.out.println(l);
                                            }
                                            System.out.printf("\t\t--%d/%d--\n", indice,
                                                    pag.getTexto().size() /
                                                            (pag.getLinha_final() - pag.getLinha_inicial()) + 1);

                                            System.out.println("[1] Para p�gina anterior");
                                            System.out.println("[2] Para pr�xima p�gina");
                                            System.out.println("[3] Para voltar ao menu");
                                            System.out.print("Insira a op��o desejada: ");
                                            continuar = Integer.parseInt(in.nextLine());

                                            if (continuar == 1) {
                                                if (pag.getLinha_inicial() > 0) {
                                                    pag.voltarPagina();
                                                    indice -= 1;
                                                }
                                                else {
                                                    System.out.println("A��o inv�lida.");
                                                    standby();
                                                }
                                            }
                                            else if (continuar == 2) {
                                                if (pag.getLinha_final() <= pag.getTexto().size()) {
                                                    pag.avancarPagina();
                                                    indice += 1;
                                                }
                                                else {
                                                    System.out.println("A��o inv�lida.");
                                                    standby();
                                                }
                                            }
                                            else if (continuar == 3) {
                                                System.out.println("Retornando...");
                                                Thread.sleep(300);
                                            }
                                            else {
                                                System.out.println("Insira uma op��o v�lida!");
                                            }
                                        } while (continuar != 3);

                                    }
                                    case 3 -> {
                                        boolean sucesso = false;
                                        do {
                                            try {
                                                System.out.print("Insira o nome do produto desejado: ");
                                                String nome = in.nextLine().toUpperCase();

                                                produtos.stream()
                                                        .filter(e -> e.get(0).equals(nome))
                                                        .forEach(e -> System.out.printf("%s...........R$%s%n%n",
                                                                                        e.get(0), e.get(1)));
                                                if (!produtos.stream().
                                                        filter(e -> e.get(0).equals(nome)).toList().isEmpty())
                                                {
                                                    System.out.print("Insira a quantidade desejada: ");
                                                    int qtd = Integer.parseInt(in.nextLine());

                                                    user.getCarrinho().adicionarProduto(nome, qtd);
                                                    System.out.println("Produto adicionado com sucesso.");
                                                    sucesso = true;
                                                } else throw new ProdutoNaoEncontrado();

                                            } catch (ProdutoNaoEncontrado e) {
                                                System.out.println("Produto n�o encontrado! Tente novamente.");
                                            }
                                        } while (!sucesso);
                                    }
                                    case 4 -> {
                                        user.exibirCarrinho();

                                        do {
                                            mostrarMenuCarrinho();
                                            escolha_carrinho = Integer.parseInt(in.nextLine());
                                            switch (escolha_carrinho) {
                                                case 1 -> {
                                                    System.out.println("Retornando...");
                                                    Thread.sleep(500);
                                                }
                                                case 2 -> {
                                                    do {
                                                        System.out.println("""
                                                                
                                                                A��es dispon�veis:
                                                                [1] Editar produto
                                                                [2] Remover produto""");
                                                        System.out.print("Insira a op��o desejada: ");
                                                        escolha_editar = Integer.parseInt(in.nextLine());

                                                        switch (escolha_editar) {
                                                            case 1 -> {
                                                                boolean sucesso = false;
                                                                do {
                                                                    try {
                                                                        System.out.print("Insira o nome do " +
                                                                                "produto a editar: ");
                                                                        String nome = in.nextLine().toUpperCase();
                                                                        System.out.print("Insira a nova quantidade: ");
                                                                        int qtd = Integer.parseInt(in.nextLine());
                                                                        user.getCarrinho().editarProduto(nome, qtd);
                                                                        System.out.println("Produto modificado" +
                                                                                " com sucesso.");
                                                                        standby();
                                                                        sucesso = true;
                                                                    } catch (ProdutoNaoEncontrado e) {
                                                                        System.out.println("Produto n�o encontrado.");
                                                                    }
                                                                } while (!sucesso);

                                                            }
                                                            case 2 -> {
                                                                boolean sucesso = false;
                                                                do {
                                                                    try {
                                                                        System.out.print("Insira o nome do produto" +
                                                                                " a remover: ");
                                                                        String nome = in.nextLine().toUpperCase();
                                                                        user.getCarrinho().removerProduto(nome);
                                                                        System.out.println("Produto removido " +
                                                                                "com sucesso.");
                                                                        standby();
                                                                        sucesso = true;

                                                                    } catch (ProdutoNaoEncontrado e) {
                                                                        System.out.println("Produto n�o encontrado.");
                                                                    }
                                                                } while (!sucesso);
                                                            }
                                                            default -> {
                                                                System.out.println("Op��o inv�lida. Tente novamente");
                                                                standby();
                                                            }
                                                        }
                                                    } while (!Arrays.asList(1, 2).contains(escolha_editar));


                                                }
                                                case 3 -> {

                                                }
                                                default -> {
                                                    System.out.println("Op��o inv�lida. Tente novamente");
                                                    standby();
                                                }
                                            }
                                        } while (!Arrays.asList(1, 2, 3).contains(escolha_carrinho));
                                    }
                                    case 5 -> System.out.println("Voltando...");

                                    default -> System.out.println("Op��o inv�lida! Tente novamente.");
                                }
                            } while (escolha_compras != 5);
                        }
                        case 2 -> {
                            System.out.println("Usu�rio desconectado.");
                            Thread.sleep(1000);
                            System.out.println("Por favor, fa�a login novamente.\n");
                            Thread.sleep(1000);
                            user = null;
                        }
                        case 3 -> {
                            System.out.println("SUPERMERCADO TXT, V2.");
                            System.out.println("Criado por Gustavo Munhoz Corr�a.");
                            standby();
                        }
                        case 4 -> {
                            System.out.println("Finalizando...");
                            Thread.sleep(500);
                            System.out.println("At� a pr�xima!");
                            sair = true;
                        }
                        default -> System.out.println("Op��o inv�lida! Tente novamente.");
                    }
                } else {
                    mostrarMenuAdmin();
                }
            }
        } while (!sair);
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

    public static void standby() {
        Scanner in = new Scanner(System.in);
        String continuar = null;
        System.out.print("Pressione ENTER para continuar...");
        continuar = in.nextLine();
    }
}

