import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {
    private String nome;
    private String email;
    private BigDecimal saldo;
    private final List<Game> gamesComprados;

    public Cliente(String nome, String email, BigDecimal saldoInicial) {
        this.nome = nome;
        this.email = email;
        this.saldo = saldoInicial;
        this.gamesComprados = new ArrayList<>();
    }

    
    public BigDecimal getSaldo() {
        return saldo;
    }

    
    public List<Game> getGamesComprados() {
        return Collections.unmodifiableList(gamesComprados);
    }

    
    public void depositar(BigDecimal valor) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldo = this.saldo.add(valor);
        }
    }

   
    public boolean comprarGame(Game game) {
        if (game == null) {
            return false;
        }
      
        if (saldo.compareTo(game.getPreco()) >= 0 && !gamesComprados.contains(game)) {
            saldo = saldo.subtract(game.getPreco());
            gamesComprados.add(game);
            return true;
        }
        return false;
    }
}