import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class Game {
    private String nome;
    private BigDecimal preco;
    private String categoria;
    private int classificacaoEtaria;
    
    private int totalAvaliacoes = 0;
    private int somaNotas = 0;

    private static final NumberFormat MOEDA_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public Game(String nome, BigDecimal preco, String categoria, int classificacaoEtaria) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do jogo não pode ser vazio.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
        this.classificacaoEtaria = classificacaoEtaria;
    }


    public String getNome() { return nome; }
    public BigDecimal getPreco() { return preco; }
    public String getCategoria() { return categoria; }
    public int getClassificacaoEtaria() { return classificacaoEtaria; }
    public int getQuantidadeAvaliacoes() { return totalAvaliacoes; }

    
    public void avaliar(int nota) {
        if (nota >= 1 && nota <= 5) {
            somaNotas += nota;
            totalAvaliacoes++;
        }
    }

   
    public double getMediaAvaliacoes() {
        if (totalAvaliacoes == 0) {
            return 0.0;
        }
        return (double) somaNotas / totalAvaliacoes;
    }

    
    @Override
    public String toString() {
        return String.format(
            "<html><b>%s</b><br><i>%s</i> | %s | ⭐ %.1f (%d)</html>",
            nome,
            categoria,
            MOEDA_FORMAT.format(preco),
            getMediaAvaliacoes(),
            getQuantidadeAvaliacoes()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return nome.equalsIgnoreCase(game.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome.toLowerCase());
    }
}