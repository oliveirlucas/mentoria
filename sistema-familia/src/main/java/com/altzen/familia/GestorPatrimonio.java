package com.altzen.familia;

import java.util.ArrayList;
import java.util.Scanner;

public class GestorPatrimonio {

    public static void listarPessoas(ArrayList<Pessoa> familia) {
        for (int i = 0; i < familia.size(); i++) {
            Pessoa pessoa = familia.get(i);
            System.out.printf("%d - %s (%s) | Carteira: R$ %.2f | Dívida: R$ %.2f%n",
                    (i + 1), pessoa.nome, pessoa.parentesco, pessoa.carteira, pessoa.divida);
        }
    }

    public static Pessoa escolherPessoa(Scanner scanner, ArrayList<Pessoa> familia, String titulo) {
        if (familia.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
            return null;
        }

        System.out.println("\n" + titulo);
        listarPessoas(familia);
        System.out.print("Número da pessoa (0 = cancelar): ");

        int numero = lerIntSeguro(scanner);
        if (numero == Integer.MIN_VALUE) {
            return null;
        }

        if (numero == 0) {
            System.out.println("Operação cancelada.");
            return null;
        }

        if (numero < 1 || numero > familia.size()) {
            System.out.println("Número de pessoa inválido.");
            return null;
        }

        return familia.get(numero - 1);
    }

    public static Pessoa escolherProprietarioNoCadastro(Scanner scanner, ArrayList<Pessoa> familia, String tipoBem) {
        if (familia.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada. O " + tipoBem + " será cadastrado sem proprietário.");
            return null;
        }

        System.out.println("\nProprietário do " + tipoBem + " (0 = sem proprietário):");
        listarPessoas(familia);
        System.out.print("Número: ");

        int numero = lerIntSeguro(scanner);
        if (numero == Integer.MIN_VALUE) {
            System.out.println("Entrada inválida. O bem será cadastrado sem proprietário.");
            return null;
        }

        if (numero == 0) {
            return null;
        }

        if (numero < 1 || numero > familia.size()) {
            System.out.println("Número inválido. O bem será cadastrado sem proprietário.");
            return null;
        }

        Pessoa escolhida = familia.get(numero - 1);

        if (("casa".equals(tipoBem) || "carro".equals(tipoBem)) && !escolhida.podeSerProprietarioDeBemImovelOuVeiculo()) {
            System.out.println("Atenção: " + escolhida.nome + " é menor de idade e não pode ser proprietário de "
                    + tipoBem + ". O bem ficará sem proprietário.");
            return null;
        }

        return escolhida;
    }

    public static boolean confirmarAcao(Scanner scanner, String mensagem) {
        System.out.print(mensagem + " (S/N): ");
        String resposta = scanner.nextLine().trim().toUpperCase();
        return resposta.equals("S") || resposta.equals("SIM");
    }

    public static boolean validarTransferencia(Pessoa donoAtual, Pessoa novoDono, String tipoBem, String nomeBem) {
        if (donoAtual == null) {
            System.out.println("Este " + tipoBem + " não possui proprietário. Use a opção de atribuir proprietário antes.");
            return false;
        }

        if (novoDono == donoAtual) {
            System.out.println("O novo proprietário deve ser diferente do atual (" + donoAtual.nome + ").");
            return false;
        }

        if (("casa".equals(tipoBem) || "carro".equals(tipoBem)) && !novoDono.podeSerProprietarioDeBemImovelOuVeiculo()) {
            System.out.println(novoDono.nome + " é menor de idade e não pode receber a propriedade de um " + tipoBem + ".");
            return false;
        }

        return true;
    }

    public static void transferirCasa(Scanner scanner, ArrayList<Pessoa> familia, ArrayList<Casa> casas) {
        if (casas.isEmpty()) {
            System.out.println("Nenhuma casa cadastrada.");
            return;
        }
        if (familia.size() < 2) {
            System.out.println("É necessário ter pelo menos duas pessoas cadastradas para transferir uma casa.");
            return;
        }

        System.out.println("\n=== TRANSFERIR CASA ===");
        for (int i = 0; i < casas.size(); i++) {
            Casa c = casas.get(i);
            String donoTxt = c.nomeDono() != null ? c.nomeDono() : "sem proprietário";
            System.out.printf("%d - %s (dono atual: %s) - R$ %.2f%n", (i + 1), c.nome, donoTxt, c.valor);
        }

        System.out.print("Número da casa (0 = cancelar): ");
        int numCasa = lerIntSeguro(scanner);
        if (numCasa == Integer.MIN_VALUE) {
            return;
        }

        if (numCasa == 0) {
            System.out.println("Transferência cancelada.");
            return;
        }
        if (numCasa < 1 || numCasa > casas.size()) {
            System.out.println("Número de casa inválido.");
            return;
        }

        Casa casa = casas.get(numCasa - 1);
        Pessoa donoAtual = casa.dono;

        Pessoa novoDono = escolherPessoa(scanner, familia, "Novo proprietário da casa \"" + casa.nome + "\":");
        if (novoDono == null) {
            return;
        }

        if (!validarTransferencia(donoAtual, novoDono, "casa", casa.nome)) {
            return;
        }

        executarVenda(scanner, "casa", casa.nome, casa.valor, donoAtual, novoDono, dono -> casa.setDono(dono));
    }

    public static void transferirCarro(Scanner scanner, ArrayList<Pessoa> familia, ArrayList<Carro> carros) {
        if (carros.isEmpty()) {
            System.out.println("Nenhum carro cadastrado.");
            return;
        }
        if (familia.size() < 2) {
            System.out.println("É necessário ter pelo menos duas pessoas cadastradas para transferir um carro.");
            return;
        }

        System.out.println("\n=== TRANSFERIR CARRO ===");
        for (int i = 0; i < carros.size(); i++) {
            Carro c = carros.get(i);
            String donoTxt = c.nomeDono() != null ? c.nomeDono() : "sem proprietário";
            System.out.printf("%d - %s (dono atual: %s) - R$ %.2f%n", (i + 1), c.nome, donoTxt, c.valor);
        }

        System.out.print("Número do carro (0 = cancelar): ");
        int numCarro = lerIntSeguro(scanner);
        if (numCarro == Integer.MIN_VALUE) {
            return;
        }

        if (numCarro == 0) {
            System.out.println("Transferência cancelada.");
            return;
        }
        if (numCarro < 1 || numCarro > carros.size()) {
            System.out.println("Número de carro inválido.");
            return;
        }

        Carro carro = carros.get(numCarro - 1);
        Pessoa donoAtual = carro.dono;

        Pessoa novoDono = escolherPessoa(scanner, familia, "Novo proprietário do carro \"" + carro.nome + "\":");
        if (novoDono == null) {
            return;
        }

        if (!validarTransferencia(donoAtual, novoDono, "carro", carro.nome)) {
            return;
        }

        executarVenda(scanner, "carro", carro.nome, carro.valor, donoAtual, novoDono, dono -> carro.setDono(dono));
    }

    private static void executarVenda(Scanner scanner, String tipoBem, String nomeBem, double valorBem,
            Pessoa vendedor, Pessoa comprador, java.util.function.Consumer<Pessoa> aplicarTroca) {

        double saldoComprador = comprador.carteira;
        double faltante = Math.max(0, valorBem - saldoComprador);

        System.out.println("\n--- Resumo da venda ---");
        System.out.printf("Bem: %s \"%s\" (R$ %.2f)%n", tipoBem, nomeBem, valorBem);
        System.out.printf("Vendedor: %s (carteira atual: R$ %.2f)%n", vendedor.nome, vendedor.carteira);
        System.out.printf("Comprador: %s (carteira atual: R$ %.2f, dívida atual: R$ %.2f)%n",
                comprador.nome, comprador.carteira, comprador.divida);

        double valorPago;
        double valorEmprestimo;

        if (faltante == 0) {
            valorPago = valorBem;
            valorEmprestimo = 0;
            System.out.printf("Pagamento à vista: R$ %.2f%n", valorPago);
        } else {
            System.out.printf("Saldo insuficiente. Faltam R$ %.2f para concluir a compra.%n", faltante);
            if (!confirmarAcao(scanner,
                    String.format("Deseja contratar empréstimo de R$ %.2f (sem juros) para cobrir o restante?", faltante))) {
                System.out.println("Venda cancelada por saldo insuficiente.");
                return;
            }
            valorPago = saldoComprador;
            valorEmprestimo = faltante;
            System.out.printf("Será debitado da carteira: R$ %.2f%n", valorPago);
            System.out.printf("Será adicionado à dívida: R$ %.2f%n", valorEmprestimo);
        }

        double saldoFinalComprador = comprador.carteira - valorPago;
        double dividaFinalComprador = comprador.divida + valorEmprestimo;
        double saldoFinalVendedor = vendedor.carteira + valorBem;

        System.out.println("\n--- Após a venda ---");
        System.out.printf("%s: carteira R$ %.2f, dívida R$ %.2f%n",
                comprador.nome, saldoFinalComprador, dividaFinalComprador);
        System.out.printf("%s: carteira R$ %.2f%n", vendedor.nome, saldoFinalVendedor);

        String msgConfirmacao = String.format("Confirmar venda do %s \"%s\" de %s para %s?",
                tipoBem, nomeBem, vendedor.nome, comprador.nome);

        if (!confirmarAcao(scanner, msgConfirmacao)) {
            System.out.println("Venda não realizada.");
            return;
        }

        comprador.debitar(valorPago);
        if (valorEmprestimo > 0) {
            comprador.aumentarDivida(valorEmprestimo);
        }
        vendedor.creditar(valorBem);
        aplicarTroca.accept(comprador);

        System.out.printf("Venda concluída! %s \"%s\" agora pertence a %s.%n",
                Character.toUpperCase(tipoBem.charAt(0)) + tipoBem.substring(1),
                nomeBem, comprador.nome);
    }

    public static void transferirAnimal(Scanner scanner, ArrayList<Pessoa> familia, ArrayList<Animal> animais) {
        if (animais.isEmpty()) {
            System.out.println("Nenhum animal cadastrado.");
            return;
        }
        if (familia.size() < 2) {
            System.out.println("É necessário ter pelo menos duas pessoas cadastradas para transferir um animal.");
            return;
        }

        System.out.println("\n=== TRANSFERIR ANIMAL ===");
        for (int i = 0; i < animais.size(); i++) {
            Animal animal = animais.get(i);
            String donoTxt = animal.nomeDono() != null ? animal.nomeDono() : "sem proprietário";
            System.out.println((i + 1) + " - " + animal.nome + " (dono atual: " + donoTxt + ")");
        }

        System.out.print("Número do animal (0 = cancelar): ");
        int numAnimal = lerIntSeguro(scanner);
        if (numAnimal == Integer.MIN_VALUE) {
            return;
        }

        if (numAnimal == 0) {
            System.out.println("Transferência cancelada.");
            return;
        }
        if (numAnimal < 1 || numAnimal > animais.size()) {
            System.out.println("Número de animal inválido.");
            return;
        }

        Animal animal = animais.get(numAnimal - 1);
        Pessoa donoAtual = animal.dono;

        Pessoa novoDono = escolherPessoa(scanner, familia, "Novo proprietário do animal \"" + animal.nome + "\":");
        if (novoDono == null) {
            return;
        }

        if (!validarTransferencia(donoAtual, novoDono, "animal", animal.nome)) {
            return;
        }

        String msgConfirmacao = "Tem certeza que deseja transferir o animal \"" + animal.nome + "\" de "
                + donoAtual.nome + " para " + novoDono.nome + "?";

        if (!confirmarAcao(scanner, msgConfirmacao)) {
            System.out.println("Transferência não realizada.");
            return;
        }

        animal.setDono(novoDono);
        System.out.println("Animal \"" + animal.nome + "\" transferido com sucesso para " + novoDono.nome + ".");
    }

    public static void atribuirProprietario(Scanner scanner, ArrayList<Pessoa> familia,
            ArrayList<Animal> animais, ArrayList<Casa> casas, ArrayList<Carro> carros) {

        if (familia.isEmpty()) {
            System.out.println("Cadastre pelo menos uma pessoa antes de atribuir proprietário.");
            return;
        }

        System.out.println("\n=== ATRIBUIR PROPRIETÁRIO ===");
        System.out.println("1 - Animal");
        System.out.println("2 - Casa");
        System.out.println("3 - Carro");
        System.out.println("0 - Cancelar");
        System.out.print("Tipo de bem: ");

        int tipo = lerIntSeguro(scanner);
        if (tipo == Integer.MIN_VALUE) {
            return;
        }

        if (tipo == 0) {
            System.out.println("Operação cancelada.");
            return;
        }

        Pessoa novoDono = escolherPessoa(scanner, familia, "Escolha o proprietário:");
        if (novoDono == null) {
            return;
        }

        if (tipo == 1) {
            atribuirDonoAnimal(scanner, animais, novoDono);
        } else if (tipo == 2) {
            atribuirDonoCasa(scanner, casas, novoDono);
        } else if (tipo == 3) {
            atribuirDonoCarro(scanner, carros, novoDono);
        } else {
            System.out.println("Tipo inválido.");
        }
    }

    public static void pagarDivida(Scanner scanner, ArrayList<Pessoa> familia) {
        if (familia.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
            return;
        }

        Pessoa pessoa = escolherPessoa(scanner, familia, "=== PAGAR DÍVIDA ===");
        if (pessoa == null) {
            return;
        }

        if (!pessoa.temDivida()) {
            System.out.println(pessoa.nome + " não possui dívida.");
            return;
        }

        if (pessoa.carteira <= 0) {
            System.out.printf("%s não possui saldo na carteira para pagar a dívida (Dívida: R$ %.2f).%n",
                    pessoa.nome, pessoa.divida);
            return;
        }

        double maximoPagavel = Math.min(pessoa.divida, pessoa.carteira);
        System.out.printf("%nCarteira: R$ %.2f | Dívida: R$ %.2f%n", pessoa.carteira, pessoa.divida);
        System.out.printf("Valor máximo que pode ser pago agora: R$ %.2f%n", maximoPagavel);
        System.out.print("Valor a pagar (0 = cancelar): ");

        if (!scanner.hasNextDouble()) {
            System.out.println("Valor inválido. Operação cancelada.");
            scanner.nextLine();
            return;
        }

        double valor = scanner.nextDouble();
        scanner.nextLine();

        if (valor == 0) {
            System.out.println("Operação cancelada.");
            return;
        }

        if (valor < 0) {
            System.out.println("O valor não pode ser negativo.");
            return;
        }

        if (valor > maximoPagavel) {
            System.out.printf("Valor maior que o permitido. Máximo: R$ %.2f.%n", maximoPagavel);
            return;
        }

        double saldoFinal = pessoa.carteira - valor;
        double dividaFinal = pessoa.divida - valor;
        String msg = String.format(
                "Confirmar pagamento de R$ %.2f da dívida de %s? (Após: carteira R$ %.2f, dívida R$ %.2f)",
                valor, pessoa.nome, saldoFinal, dividaFinal);

        if (!confirmarAcao(scanner, msg)) {
            System.out.println("Pagamento não realizado.");
            return;
        }

        pessoa.pagarDivida(valor);
        System.out.printf("Pagamento realizado! Carteira: R$ %.2f | Dívida: R$ %.2f%n",
                pessoa.carteira, pessoa.divida);
    }

    private static void atribuirDonoAnimal(Scanner scanner, ArrayList<Animal> animais, Pessoa novoDono) {
        if (animais.isEmpty()) {
            System.out.println("Nenhum animal cadastrado.");
            return;
        }

        Animal animal = escolherAnimal(scanner, animais);
        if (animal == null) {
            return;
        }

        if (animal.dono == novoDono) {
            System.out.println(novoDono.nome + " já é o proprietário deste animal.");
            return;
        }

        String de = animal.dono != null ? animal.dono.nome : "ninguém";
        String msg = "Atribuir o animal \"" + animal.nome + "\" de " + de + " para " + novoDono.nome + "?";

        if (!confirmarAcao(scanner, msg)) {
            System.out.println("Atribuição não realizada.");
            return;
        }

        animal.setDono(novoDono);
        System.out.println("Proprietário do animal \"" + animal.nome + "\" atualizado para " + novoDono.nome + ".");
    }

    private static void atribuirDonoCasa(Scanner scanner, ArrayList<Casa> casas, Pessoa novoDono) {
        if (casas.isEmpty()) {
            System.out.println("Nenhuma casa cadastrada.");
            return;
        }

        if (!novoDono.podeSerProprietarioDeBemImovelOuVeiculo()) {
            System.out.println(novoDono.nome + " é menor de idade e não pode ser proprietário de casa.");
            return;
        }

        Casa casa = escolherCasa(scanner, casas);
        if (casa == null) {
            return;
        }

        if (casa.dono == novoDono) {
            System.out.println(novoDono.nome + " já é o proprietário desta casa.");
            return;
        }

        String de = casa.dono != null ? casa.dono.nome : "ninguém";
        String msg = "Atribuir a casa \"" + casa.nome + "\" de " + de + " para " + novoDono.nome + "?";

        if (!confirmarAcao(scanner, msg)) {
            System.out.println("Atribuição não realizada.");
            return;
        }

        casa.setDono(novoDono);
        System.out.println("Proprietário da casa \"" + casa.nome + "\" atualizado para " + novoDono.nome + ".");
    }

    private static void atribuirDonoCarro(Scanner scanner, ArrayList<Carro> carros, Pessoa novoDono) {
        if (carros.isEmpty()) {
            System.out.println("Nenhum carro cadastrado.");
            return;
        }

        if (!novoDono.podeSerProprietarioDeBemImovelOuVeiculo()) {
            System.out.println(novoDono.nome + " é menor de idade e não pode ser proprietário de carro.");
            return;
        }

        Carro carro = escolherCarro(scanner, carros);
        if (carro == null) {
            return;
        }

        if (carro.dono == novoDono) {
            System.out.println(novoDono.nome + " já é o proprietário deste carro.");
            return;
        }

        String de = carro.dono != null ? carro.dono.nome : "ninguém";
        String msg = "Atribuir o carro \"" + carro.nome + "\" de " + de + " para " + novoDono.nome + "?";

        if (!confirmarAcao(scanner, msg)) {
            System.out.println("Atribuição não realizada.");
            return;
        }

        carro.setDono(novoDono);
        System.out.println("Proprietário do carro \"" + carro.nome + "\" atualizado para " + novoDono.nome + ".");
    }

    private static Animal escolherAnimal(Scanner scanner, ArrayList<Animal> animais) {
        for (int i = 0; i < animais.size(); i++) {
            Animal a = animais.get(i);
            String donoTxt = a.nomeDono() != null ? a.nomeDono() : "sem proprietário";
            System.out.println((i + 1) + " - " + a.nome + " (" + donoTxt + ")");
        }
        System.out.print("Número (0 = cancelar): ");
        int n = lerIntSeguro(scanner);
        if (n == Integer.MIN_VALUE) {
            return null;
        }
        if (n == 0) {
            System.out.println("Operação cancelada.");
            return null;
        }
        if (n < 1 || n > animais.size()) {
            System.out.println("Número inválido.");
            return null;
        }
        return animais.get(n - 1);
    }

    private static Casa escolherCasa(Scanner scanner, ArrayList<Casa> casas) {
        for (int i = 0; i < casas.size(); i++) {
            Casa c = casas.get(i);
            String donoTxt = c.nomeDono() != null ? c.nomeDono() : "sem proprietário";
            System.out.printf("%d - %s (%s) - R$ %.2f%n", (i + 1), c.nome, donoTxt, c.valor);
        }
        System.out.print("Número (0 = cancelar): ");
        int n = lerIntSeguro(scanner);
        if (n == Integer.MIN_VALUE) {
            return null;
        }
        if (n == 0) {
            System.out.println("Operação cancelada.");
            return null;
        }
        if (n < 1 || n > casas.size()) {
            System.out.println("Número inválido.");
            return null;
        }
        return casas.get(n - 1);
    }

    private static Carro escolherCarro(Scanner scanner, ArrayList<Carro> carros) {
        for (int i = 0; i < carros.size(); i++) {
            Carro c = carros.get(i);
            String donoTxt = c.nomeDono() != null ? c.nomeDono() : "sem proprietário";
            System.out.printf("%d - %s (%s) - R$ %.2f%n", (i + 1), c.nome, donoTxt, c.valor);
        }
        System.out.print("Número (0 = cancelar): ");
        int n = lerIntSeguro(scanner);
        if (n == Integer.MIN_VALUE) {
            return null;
        }
        if (n == 0) {
            System.out.println("Operação cancelada.");
            return null;
        }
        if (n < 1 || n > carros.size()) {
            System.out.println("Número inválido.");
            return null;
        }
        return carros.get(n - 1);
    }

    private static int lerIntSeguro(Scanner scanner) {
        if (!scanner.hasNextInt()) {
            System.out.println("Entrada inválida.");
            scanner.nextLine();
            return Integer.MIN_VALUE;
        }
        int v = scanner.nextInt();
        scanner.nextLine();
        return v;
    }
}
