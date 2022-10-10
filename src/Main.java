import br.pucpr.databases.DataLoader;
import br.pucpr.system.*;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static final String RESET = "\u001B[0m";
    public static final String VERMELHO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARELO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String ROXO = "\u001B[35m";

    public static final String CIANO = "\u001B[36m";


    public static void main(String[] args) throws InterruptedException {
        Locale.setDefault(new Locale("en", "US")); // o programa estava formatando doubles com ","
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
                System.out.println(CIANO + "Bem vindo ao novo e refatorado supermercado TXT!" +
                        "\nPor favor, faça login abaixo." + RESET);

                System.out.print("Digite seu CPF (somente números): ");
                String login = in.nextLine();
                System.out.print("Digite sua senha: ");
                String senha = in.nextLine();
                user = new Usuario(login, senha);

                if (!user.existeUsuario()) {
                    int tentativas = 0;
                    System.out.println("Usuário inexsistente! Deseja se cadastrar?");
                    System.out.println("[1] Sim\n[2] Não");
                    System.out.print("Insira a opção desejada: ");
                    String tentativa = in.nextLine();

                    while (!tentativa.equals("1") && !tentativa.equals("2")) {
                        System.out.println("Insira uma opção válida!");
                        System.out.println("Usuário inexsistente! Deseja se cadastrar?");
                        System.out.println("[1] Sim\n[2] Não");
                        System.out.print("Insira a opção desejada: ");
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

                            if (tentativas == 2) {
                                System.out.println("Número máximo de tentativas excedido. Finalizando...");
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
                                            System.out.printf(AMARELO + "\n\t   --PÁGINA %d--\n", indice);
                                            for (String l : pag.mostrarPagina()) {
                                                System.out.println(l);
                                            }
                                            System.out.printf("\t\t--%d/%d--\n" + RESET, indice,
                                                    pag.getTexto().size() /
                                                            (pag.getLinha_final() - pag.getLinha_inicial()) + 1);

                                            System.out.println("[1] Para página anterior");
                                            System.out.println("[2] Para próxima página");
                                            System.out.println("[3] Para voltar ao menu");
                                            System.out.print("Insira a opção desejada: ");
                                            continuar = Integer.parseInt(in.nextLine());

                                            if (continuar == 1) {
                                                if (pag.getLinha_inicial() > 0) {
                                                    pag.voltarPagina();
                                                    indice -= 1;
                                                }
                                                else {
                                                    System.out.println("Ação inválida.");
                                                    standby();
                                                }
                                            }
                                            else if (continuar == 2) {
                                                if (pag.getLinha_final() <= pag.getTexto().size()) {
                                                    pag.avancarPagina();
                                                    indice += 1;
                                                }
                                                else {
                                                    System.out.println("Ação inválida.");
                                                    standby();
                                                }
                                            }
                                            else if (continuar == 3) {
                                                System.out.println("Retornando...");
                                                Thread.sleep(300);
                                            }
                                            else {
                                                System.out.println("Insira uma opção válida!");
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
                                                    System.out.println(VERDE + "Produto adicionado com sucesso." + RESET);
                                                    standby();
                                                    sucesso = true;
                                                } else throw new ProdutoNaoEncontrado();

                                            } catch (ProdutoNaoEncontrado e) {
                                                System.out.println("Produto não encontrado! Tente novamente.");
                                            }
                                        } while (!sucesso);
                                    }
                                    case 4 -> {
                                        System.out.print(AZUL);
                                        user.exibirCarrinho();
                                        System.out.println("------------------");
                                        System.out.println(RESET);
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
                                                                
                                                                Ações disponíveis:
                                                                [1] Editar produto
                                                                [2] Remover produto""");
                                                        System.out.print("Insira a opção desejada: ");
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
                                                                        System.out.println( VERDE + "Produto modificado" +
                                                                                " com sucesso."+ RESET);
                                                                        standby();
                                                                        sucesso = true;
                                                                    } catch (ProdutoNaoEncontrado e) {
                                                                        System.out.println("Produto não encontrado.");
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
                                                                        System.out.println(VERDE + "Produto removido " +
                                                                                "com sucesso." + RESET);
                                                                        standby();
                                                                        sucesso = true;

                                                                    } catch (ProdutoNaoEncontrado e) {
                                                                        System.out.println("Produto não encontrado.");
                                                                    }
                                                                } while (!sucesso);
                                                            }
                                                            default -> {
                                                                System.out.println("Opção inválida. Tente novamente");
                                                                standby();
                                                            }
                                                        }
                                                    } while (!Arrays.asList(1, 2).contains(escolha_editar));

                                                }
                                                case 3 -> {
                                                    try {
                                                        System.out.println("Finalizando compra...");
                                                        var carrinho = user.getCarrinho().getProdutos();
                                                        for (var p : produtos) {
                                                            if (carrinho.containsKey(p.get(0))) {
                                                                int novoEstoque = Integer.parseInt(p.get(2))
                                                                        - (Integer) carrinho.get(p.get(0)).get(0);
                                                                if (novoEstoque > 0) {
                                                                    p.set(2, "%d".formatted(novoEstoque));
                                                                } else {
                                                                    throw new QuantInvalidaException("%s em estoque: %s"
                                                                            .formatted(p.get(0), p.get(2)));
                                                                }
                                                            }
                                                        }
                                                        try (FileWriter fw = new
                                                                FileWriter("src/br/pucpr/databases/products.csv",
                                                                StandardCharsets.UTF_8)) {
                                                            StringBuilder novo_db = new StringBuilder();
                                                            for (var p : produtos) {
                                                                novo_db.append("%s;%s;%s\n".formatted(
                                                                        p.get(0), p.get(1), p.get(2)));
                                                            }
                                                            fw.write("PRODUTO;PREÇO;ESTOQUE\n" + novo_db);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }

                                                        try (FileWriter fw = new
                                                                FileWriter("src/br/pucpr/databases/users.csv",
                                                                StandardCharsets.UTF_8)) {
                                                            for (var u : usuarios) {
                                                                if (u.get(0).equals(user.getLogin())) {
                                                                    String oldCompras = u.get(2).substring(1,
                                                                            u.get(2).indexOf("|"));
                                                                    if (!oldCompras.equals("")) {
                                                                        String newCompras = oldCompras.concat(",%s"
                                                                                .formatted(
                                                                                        user.getCarrinho().toString()));

                                                                        u.set(2, "{%s|%.2f}".formatted(
                                                                                newCompras,
                                                                                user.getCarrinho().getPrecoTotal() +
                                                                                        user.getGasto()));
                                                                    } else {
                                                                        u.set(2, "{%s|%.2f}".formatted(
                                                                                user.getCarrinho().toString(),
                                                                                user.getCarrinho().getPrecoTotal() +
                                                                                        user.getGasto()));
                                                                    }
                                                                }
                                                            }
                                                            StringBuilder novo_db = new StringBuilder();
                                                            for (var u : usuarios) {
                                                                novo_db.append("%s;%s;%s\n".formatted(
                                                                        u.get(0), u.get(1), u.get(2)));
                                                            }
                                                            fw.write("LOGIN;SENHA;HISTÓRICO\n" + novo_db);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Thread.sleep(1000);
                                                        System.out.println(VERDE + "Compra finalizada com sucesso.");
                                                        Thread.sleep(1500);
                                                        System.out.println("Obrigado e até a próxima!" + RESET);
                                                        Thread.sleep(1000);
                                                        System.exit(0);

                                                    } catch (QuantInvalidaException e) {
                                                        System.out.println(VERMELHO +
                                                                "ERRO! Não há estoque suficiente.");
                                                        System.out.println(e.getMessage() + RESET);
                                                        standby();
                                                    }
                                                }
                                                default -> {
                                                    System.out.println("Opção inválida. Tente novamente");
                                                    standby();
                                                }
                                            }
                                        } while (!Arrays.asList(1, 2, 3).contains(escolha_carrinho));
                                    }
                                    case 5 -> System.out.println("Voltando...");

                                    default -> System.out.println("Opção inválida! Tente novamente.");
                                }
                            } while (escolha_compras != 5);
                        }
                        case 2 -> {
                            System.out.println("Usuário desconectado.");
                            Thread.sleep(1000);
                            System.out.println("Por favor, faça login novamente.\n");
                            Thread.sleep(1000);
                            user = null;
                        }
                        case 3 -> {
                            System.out.println(ROXO + "SUPERMERCADO TXT, V2." + RESET);
                            System.out.println("Criado por" + CIANO +  " Gustavo Munhoz Corrêa." + RESET);
                            standby();
                        }
                        case 4 -> {
                            System.out.println("Finalizando...");
                            Thread.sleep(500);
                            System.out.println("Até a próxima!");
                            sair = true;
                        }
                        default -> System.out.println("Opção inválida! Tente novamente.");
                    }
                } else {
                    mostrarMenuAdmin();
                    escolha_admin = Integer.parseInt(in.nextLine());

                    switch (escolha_admin) {
                        case 1 -> {
                            System.out.print("Deseja salvar o relatório? [S/N] ");
                            String salvar = in.nextLine().toUpperCase();
                            while (!Arrays.asList("S","N").contains(salvar)) {
                                System.out.println("Opção inválida! Tente novamente.");
                                System.out.print("Deseja salvar o relatório?");
                                salvar = in.nextLine().toUpperCase();
                            }
                            imprimirRelatorio(salvar.equals("S"));
                            standby();
                        }
                        case 2 -> {
                            System.out.print("Insira o nome do produto a cadastrar: ");
                            String nome = in.nextLine().toUpperCase();
                            boolean existe = false;
                            for (var p : produtos) {
                                if (p.get(0).equals(nome)) {
                                    existe = true;
                                    break;
                                }
                            }
                            if (!existe) {
                                System.out.print("Insira o preço: ");
                                double preco = Double.parseDouble(in.nextLine());
                                if (preco >= 0) {
                                    System.out.print("Insira o estoque inicial: ");
                                    int estoque = Integer.parseInt(in.nextLine());
                                    if (estoque > 0) {
                                        List<String> produto = Arrays.asList(nome, "%.2f".formatted(preco),
                                                "%d".formatted(estoque));
                                        produtos.add(produto);
                                        try (FileWriter fw = new
                                                FileWriter("src/br/pucpr/databases/products.csv",
                                                StandardCharsets.UTF_8,
                                                true))
                                        {
                                            fw.write("%s;%.2f;%d\n".formatted(nome, preco, estoque));
                                            fw.flush();
                                            System.out.println(VERDE + "Produto cadastrado com sucesso." + RESET);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else System.out.println("Estoque inválido.");
                                } else System.out.println("Preço inválido.");
                            } else System.out.println("Produto já cadastrado.");
                            standby();
                        }
                        case 3 -> {
                            System.out.print("Insira o nome do produto: ");
                            String nome = in.nextLine().toUpperCase();
                            boolean existe = false;
                            int index = 0;
                            for (var p : produtos) {
                                if (p.get(0).equals(nome)) {
                                    existe = true;
                                    index = produtos.indexOf(p);
                                    break;
                                }
                            }
                            if (existe) {
                                System.out.print("Insira a quantidade a adicionar: ");
                                int qtd = Integer.parseInt(in.nextLine());
                                if (qtd > 0) {
                                    int novoEstoque = Integer.parseInt(produtos.get(index).get(2)) + qtd;
                                    produtos.get(index).set(2, "%d".formatted(novoEstoque));
                                    try (FileWriter fw = new
                                            FileWriter("src/br/pucpr/databases/products.csv",
                                            StandardCharsets.UTF_8))
                                    {
                                        StringBuilder novo_db = new StringBuilder();
                                        for (var p : produtos) {
                                            novo_db.append("%s;%s;%s\n".formatted(
                                                    p.get(0), p.get(1), p.get(2)));
                                        }
                                        fw.write("PRODUTO;PREÇO;ESTOQUE\n" + novo_db);
                                        System.out.println(VERDE + "Estoque adicionado com sucesso." + RESET);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else System.out.println("Quantidade inválida.");
                            } else System.out.println("Produto não encontrado.");
                            standby();
                        }
                        case 4 -> {
                            System.out.println("Finalizando...");
                            sair = true;
                        }
                        default -> System.out.println("Insira uma opção válida!");
                    }
                }
            }
        } while (!sair);
    }

    static void mostrarMenuPrincipal() {
        System.out.println(VERMELHO + "\n---MENU PRINCIPAL---" + RESET);
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Fazer compras");
        System.out.println("[2] Trocar usuário");
        System.out.println("[3] Sobre");
        System.out.println("[4] Sair");
        System.out.print("Insira a opção desejada: ");
    }

    static void mostrarMenuCompras() {
        System.out.println(VERDE + "\n---MENU DE COMPRAS---" + RESET);
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Buscar produto");
        System.out.println("[2] Listar produtos");
        System.out.println("[3] Adicionar produto ao carrinho");
        System.out.println("[4] Exibir carrinho");
        System.out.println("[5] Voltar ao menu principal");
        System.out.print("Insira a opção desejada: ");
    }

    static void mostrarMenuCarrinho() {
        System.out.println("[1] Continuar comprando");
        System.out.println("[2] Editar carrinho");
        System.out.println("[3] Finalizar compra");
        System.out.print("Insira a opção desejada: ");
    }

    /**
     * Imprime um menu acessível somente ao usuário administrador.
     */
    static void mostrarMenuAdmin() {
        System.out.println(AZUL + "---ADMINISTRADOR---" + RESET);
        System.out.println("O que gostaria de fazer?");
        System.out.println("[1] Relatório de clientes");
        System.out.println("[2] Cadastrar produto");
        System.out.println("[3] Adicionar estoque");
        System.out.println("[4] Sair");
        System.out.print("Insira a opção desejada: ");
    }

    public static void standby() {
        Scanner in = new Scanner(System.in);
        String continuar = null;
        System.out.print("Pressione ENTER para continuar...");
        continuar = in.nextLine();
    }

    public static void imprimirRelatorio(boolean salvar) {
        File usuarios = new File("src/br/pucpr/databases/users.csv");
        double totalGeral = 0;
        int contador = 0;
        String arquivo = "--RELATÓRIO DE VENDAS--\n";
        System.out.println("--RELATÓRIO DE VENDAS--");

        Date data = Calendar.getInstance().getTime();
        SimpleDateFormat formatador = new SimpleDateFormat("dd-MM-yyyy");
        String strData = formatador.format(data);


        try {
            Scanner sc1 = new Scanner(usuarios);
            List<List<String>> organizador = new ArrayList<>();

            while (sc1.hasNextLine()) {
                String linha = sc1.nextLine();
                List<String> dados_cliente = new ArrayList<>();
                if (!linha.substring(0, linha.indexOf(";")).equals("admin") &&
                        !linha.substring(0, linha.indexOf(";")).equals("LOGIN")) {

                    String gasto_ind = linha.substring(linha.indexOf("|") + 1, linha.length() - 1);
                    if (!gasto_ind.equals("0.0")) {
                        dados_cliente.add(linha.substring(0, linha.indexOf(";")));
                        dados_cliente.add(gasto_ind);
                        organizador.add(dados_cliente);
                        totalGeral += Double.parseDouble(gasto_ind);
                        contador += 1;
                    }
                }
            }
            ComparadorUsuarios comp = new ComparadorUsuarios();
            organizador.sort(comp.reversed());

            for (List<String> user : organizador) {
                System.out.printf("\n%s, $%s\n", user.get(0), user.get(1));
                arquivo = arquivo.concat("%s, $%s\n".formatted(user.get(0), user.get(1)));
                Scanner sc2 = new Scanner(usuarios);

                while (sc2.hasNextLine()) {
                    String linha = sc2.nextLine();
                    List<String> produtos;

                    if (!linha.substring(0, linha.indexOf(";")).equals("admin") &&
                            !linha.substring(0, linha.indexOf(";")).equals("LOGIN")) {

                        if (!linha.substring(linha.indexOf("|") + 1, linha.length() - 1).equals("0.0")) {
                            if (linha.substring(0, linha.indexOf(";")).equals(user.get(0))) {
                                produtos = List.of(linha.substring(linha.indexOf("{") + 1,
                                        linha.indexOf("|")).split(","));
                                for (String produto : produtos) {
                                    System.out.printf("\t%s\n", produto);
                                    arquivo = arquivo.concat("\t%s\n".formatted(produto));
                                }
                                arquivo = arquivo.concat("\n");
                            }
                        }

                    }
                }
            }
            arquivo = arquivo.concat("------------------\n");
            arquivo = arquivo.concat("TOTAL GERAL: $%.2f\n".formatted(totalGeral));
            arquivo = arquivo.concat("VALOR MÉDIO: $%.2f\n".formatted(totalGeral / contador));


            System.out.println("------------------");
            System.out.printf("TOTAL GERAL: $%.2f\n", totalGeral);
            System.out.printf("VALOR MÉDIO: $%.2f\n", totalGeral / contador);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (salvar) {
            try {
                PrintWriter pw = new PrintWriter("RELATORIO_%s.txt".formatted(strData));
                pw.println(arquivo);
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

