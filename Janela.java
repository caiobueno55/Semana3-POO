import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Janela {

    private final Cliente cliente;
    private final List<Game> todosOsGames;

    private JFrame frame;
    private JLabel lblSaldo;
    private DefaultListModel<Game> modelDisponiveis;
    private JList<Game> listaDisponiveis;
    private DefaultListModel<Game> modelComprados;
    private JList<Game> listaComprados;

    private JComboBox<String> cbCategoria;
    private JTextField txtPrecoMin;
    private JTextField txtPrecoMax;

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final NumberFormat MOEDA_FORMAT = NumberFormat.getCurrencyInstance(PT_BR);

    public Janela(Cliente cliente, List<Game> catalogo) {
        this.cliente = cliente;
        this.todosOsGames = new ArrayList<>(catalogo);
        criarEExibirUI();
    }

    private void criarEExibirUI() {
        
        frame = new JFrame("Loja de Games");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        ((JPanel) frame.getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        
        JPanel painelTopo = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Loja de Games - Java OOP");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblSaldo = new JLabel();
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        painelTopo.add(lblTitulo, BorderLayout.WEST);
        painelTopo.add(lblSaldo, BorderLayout.EAST);
        frame.add(painelTopo, BorderLayout.NORTH);

        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                criarPainelDeLista("Meus Games", true),
                criarPainelDeLista("Disponíveis para Compra", false));
        splitPane.setResizeWeight(0.4);
        frame.add(splitPane, BorderLayout.CENTER);

        
        frame.add(criarPainelDeAcoes(), BorderLayout.SOUTH);

        atualizarUICompleta();
        frame.setVisible(true);
    }
    
    private JPanel criarPainelDeLista(String titulo, boolean isComprados) {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(new TitledBorder(titulo));

        if (isComprados) {
            modelComprados = new DefaultListModel<>();
            listaComprados = new JList<>(modelComprados);
            listaComprados.setCellRenderer(new GameListRenderer());
            painel.add(new JScrollPane(listaComprados), BorderLayout.CENTER);
        } else {
            modelDisponiveis = new DefaultListModel<>();
            listaDisponiveis = new JList<>(modelDisponiveis);
            listaDisponiveis.setCellRenderer(new GameListRenderer());
            painel.add(new JScrollPane(listaDisponiveis), BorderLayout.CENTER);
            painel.add(criarPainelFiltros(), BorderLayout.SOUTH);
        }
        
        return painel;
    }

    private JPanel criarPainelFiltros() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new TitledBorder("Filtros"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Categoria
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cbCategoria = new JComboBox<>();
        painel.add(cbCategoria, gbc);

        // Preço
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Preço Mín:"), gbc);
        gbc.gridx = 1;
        txtPrecoMin = new JTextField(5);
        painel.add(txtPrecoMin, gbc);
        gbc.gridx = 2;
        painel.add(new JLabel("Máx:"), gbc);
        gbc.gridx = 3;
        txtPrecoMax = new JTextField(5);
        painel.add(txtPrecoMax, gbc);

        // Botões de filtro
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton btnFiltrar = new JButton("Aplicar Filtros");
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        painel.add(btnFiltrar, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        JButton btnLimpar = new JButton("Limpar Filtros");
        btnLimpar.addActionListener(e -> limparFiltros());
        painel.add(btnLimpar, gbc);

        return painel;
    }

    private JPanel criarPainelDeAcoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton btnComprar = new JButton("Comprar");
        btnComprar.addActionListener(e -> comprarGameSelecionado());
        
        JButton btnDetalhes = new JButton("Ver Detalhes");
        btnDetalhes.addActionListener(e -> exibirDetalhes());

        JButton btnAvaliar = new JButton("Avaliar");
        btnAvaliar.addActionListener(e -> avaliarGame());
        
        JButton btnAdicionarFundos = new JButton("Adicionar Fundos");
        btnAdicionarFundos.addActionListener(e -> adicionarFundos());

        JButton btnAdicionarGame = new JButton("Adicionar Game à Loja");
        btnAdicionarGame.addActionListener(e -> adicionarNovoGameNaLoja());

        painel.add(btnComprar);
        painel.add(btnDetalhes);
        painel.add(btnAvaliar);
        painel.add(btnAdicionarFundos);
        painel.add(btnAdicionarGame);

        return painel;
    }

   

    private void atualizarUICompleta() {
        
        lblSaldo.setText("Saldo: " + MOEDA_FORMAT.format(cliente.getSaldo()));

        
        String selected = (String) cbCategoria.getSelectedItem();
        cbCategoria.removeAllItems();
        cbCategoria.addItem("Todas");
        todosOsGames.stream()
            .map(Game::getCategoria)
            .distinct()
            .sorted()
            .forEach(cbCategoria::addItem);
        cbCategoria.setSelectedItem(selected != null ? selected : "Todas");

       
        aplicarFiltros();
        modelComprados.clear();
        cliente.getGamesComprados().forEach(modelComprados::addElement);
    }

    private void aplicarFiltros() {
        String categoria = (String) cbCategoria.getSelectedItem();
        BigDecimal min = parseBigDecimal(txtPrecoMin.getText());
        BigDecimal max = parseBigDecimal(txtPrecoMax.getText());
        
        List<Game> disponiveisFiltrados = todosOsGames.stream()
            .filter(g -> !cliente.getGamesComprados().contains(g)) // Não comprados
            .filter(g -> "Todas".equals(categoria) || g.getCategoria().equals(categoria))
            .filter(g -> min == null || g.getPreco().compareTo(min) >= 0)
            .filter(g -> max == null || g.getPreco().compareTo(max) <= 0)
            .collect(Collectors.toList());
        
        modelDisponiveis.clear();
        disponiveisFiltrados.forEach(modelDisponiveis::addElement);
    }

    private void limparFiltros() {
        cbCategoria.setSelectedItem("Todas");
        txtPrecoMin.setText("");
        txtPrecoMax.setText("");
        aplicarFiltros();
    }
    
    private void comprarGameSelecionado() {
        Game gameSelecionado = listaDisponiveis.getSelectedValue();
        if (gameSelecionado == null) {
            JOptionPane.showMessageDialog(frame, "Por favor, selecione um game para comprar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cliente.comprarGame(gameSelecionado)) {
            JOptionPane.showMessageDialog(frame, "Compra de '" + gameSelecionado.getNome() + "' realizada com sucesso!", "Compra Realizada", JOptionPane.INFORMATION_MESSAGE);
            atualizarUICompleta();
        } else {
            JOptionPane.showMessageDialog(frame, "Saldo insuficiente para comprar '" + gameSelecionado.getNome() + "'.", "Erro na Compra", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirDetalhes() {
        Game gameSelecionado = listaDisponiveis.getSelectedValue();
        if (gameSelecionado == null) {
            gameSelecionado = listaComprados.getSelectedValue();
        }
        if(gameSelecionado == null) {
             JOptionPane.showMessageDialog(frame, "Selecione um game para ver os detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
             return;
        }

        String detalhes = String.format(
            "Nome: %s\nCategoria: %s\nClassificação: %d+\nPreço: %s\nAvaliação Média: %.1f (%d votos)",
            gameSelecionado.getNome(),
            gameSelecionado.getCategoria(),
            gameSelecionado.getClassificacaoEtaria(),
            MOEDA_FORMAT.format(gameSelecionado.getPreco()),
            gameSelecionado.getMediaAvaliacoes(),
            gameSelecionado.getQuantidadeAvaliacoes()
        );
        JOptionPane.showMessageDialog(frame, detalhes, "Detalhes do Game", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void avaliarGame() {
         Game gameSelecionado = listaDisponiveis.getSelectedValue();
        if (gameSelecionado == null) {
            gameSelecionado = listaComprados.getSelectedValue();
        }
        if(gameSelecionado == null) {
             JOptionPane.showMessageDialog(frame, "Selecione um game para avaliar.", "Aviso", JOptionPane.WARNING_MESSAGE);
             return;
        }

        Integer[] notas = {1, 2, 3, 4, 5};
        Integer nota = (Integer) JOptionPane.showInputDialog(frame, "Selecione a nota para " + gameSelecionado.getNome(), 
            "Avaliar Game", JOptionPane.QUESTION_MESSAGE, null, notas, notas[4]);
        
        if (nota != null) {
            gameSelecionado.avaliar(nota);
            atualizarUICompleta();
            JOptionPane.showMessageDialog(frame, "Obrigado por avaliar!", "Avaliação Registrada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void adicionarFundos() {
        String valorStr = JOptionPane.showInputDialog(frame, "Digite o valor a ser depositado:", "Adicionar Fundos", JOptionPane.QUESTION_MESSAGE);
        if (valorStr != null) {
            BigDecimal valor = parseBigDecimal(valorStr);
            if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
                cliente.depositar(valor);
                atualizarUICompleta();
            } else {
                JOptionPane.showMessageDialog(frame, "Valor inválido. Por favor, insira um número positivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void adicionarNovoGameNaLoja() {
        
        JTextField nomeField = new JTextField();
        JTextField precoField = new JTextField();
        JTextField categoriaField = new JTextField();
        JTextField classificacaoField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nome:")); panel.add(nomeField);
        panel.add(new JLabel("Preço (ex: 99.90):")); panel.add(precoField);
        panel.add(new JLabel("Categoria:")); panel.add(categoriaField);
        panel.add(new JLabel("Classificação Etária:")); panel.add(classificacaoField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Adicionar Novo Game à Loja", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nome = nomeField.getText();
                BigDecimal preco = parseBigDecimal(precoField.getText());
                String categoria = categoriaField.getText();
                int classificacao = Integer.parseInt(classificacaoField.getText());

                if (nome.trim().isEmpty() || categoria.trim().isEmpty() || preco == null) {
                    throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
                }

                todosOsGames.add(new Game(nome, preco, categoria, classificacao));
                atualizarUICompleta();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Erro ao adicionar game: " + e.getMessage(), "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BigDecimal parseBigDecimal(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(valor.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    
    private static class GameListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Game) {
                Game game = (Game) value;
                label.setText(game.toString());
            }
            label.setBorder(new EmptyBorder(5, 5, 5, 5));
            return label;
        }
    }

   
    public static void main(String[] args) {
        
        Cliente cliente = new Cliente("Thay", "thay@email.com", new BigDecimal("200.00"));
        
        List<Game> catalogoInicial = new ArrayList<>(Arrays.asList(
            new Game("The Witcher 3", new BigDecimal("79.99"), "RPG", 18),
            new Game("Minecraft", new BigDecimal("99.90"), "Aventura", 10),
            new Game("Stardew Valley", new BigDecimal("24.99"), "Simulação", 0),
            new Game("Hades", new BigDecimal("47.49"), "Roguelike", 14),
            new Game("Celeste", new BigDecimal("36.99"), "Plataforma", 10),
            new Game("Valorant", new BigDecimal("0.00"), "FPS", 14),
            new Game("Cyberpunk 2077", new BigDecimal("199.90"), "RPG", 18),
            new Game("Among Us", new BigDecimal("10.89"), "Party", 7)
        ));

       
        SwingUtilities.invokeLater(() -> new Janela(cliente, catalogoInicial));
    }
}