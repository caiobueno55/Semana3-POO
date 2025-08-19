import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BigDecimal saldoInicial = new BigDecimal("200.00");
        Cliente cliente = new Cliente("Thay", "thay@example.com", saldoInicial);

        List<Game> disponiveis = Arrays.asList(
                new Game("Minecraft", 99.99, "Aventura", 10),
                new Game("The Witcher 3", 199.99, "RPG", 18),
                new Game("FIFA 21", 249.99, "Esporte", 0),
                new Game("Hades", 79.90, "Roguelike", 14),
                new Game("Stardew Valley", 24.99, "Simulação", 0),
                new Game("Celeste", 36.90, "Plataforma", 10),
                new Game("Among Us", 19.99, "Party", 7),
                new Game("Cyberpunk 2077", 149.99, "RPG", 18),
                new Game("Valorant", 0.00, "FPS", 14),
                new Game("Rocket League", 39.99, "Esporte", 3)
        );

        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o valor que deseja adicionar ao saldo: R$ ");
        BigDecimal valorAdicional = sc.nextBigDecimal();
        cliente.adicionarFundos(valorAdicional);

        cliente.comprarMaximo(disponiveis);
        cliente.exibirResumo();

        sc.close();
    }
}
