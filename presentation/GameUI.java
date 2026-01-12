package presentation;

import domain.*;
import javax.swing.*;

import datasource.FileGameStorage;
import datasource.GameStorage;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameUI extends JFrame {
    private final GameController controller;
    private final Game game;
    private JPanel cardsPanel;
    private JLabel player1Label;
    private JLabel player2Label;
    private JLabel currentPlayerLabel;
    private JLabel errorLabel;
    private Map<ChipColor, JButton> chipButtons;

    public GameUI(GameController controller, Game game) {
        this.controller = controller;
        this.game = game;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chip Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 700);

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        currentPlayerLabel = new JLabel("Current Player: 1", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(currentPlayerLabel);

        player1Label = new JLabel("Player 1 - VP: 0 | Chips: ", SwingConstants.CENTER);
        player1Label.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(player1Label);

        player2Label = new JLabel("Player 2 - VP: 0 | Chips: ", SwingConstants.CENTER);
        player2Label.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(player2Label);

        add(topPanel, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(3, 5, 10, 10));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(cardsPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel chipPanel = new JPanel(new FlowLayout());
        chipButtons = new HashMap<>();
        for (ChipColor color : ChipColor.values()) {
            JButton btn = new JButton(color.name());
            btn.setBackground(getColorForChip(color));
            btn.setPreferredSize(new Dimension(100, 40));
            btn.addActionListener(e -> onChipClicked(color));
            chipButtons.put(color, btn);
            chipPanel.add(btn);
        }

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setPreferredSize(new Dimension(120, 40));
        newGameBtn.addActionListener(e -> onNewGameClicked());
        chipPanel.add(newGameBtn);

        JButton loadGameBtn = new JButton("Load Game");
        loadGameBtn.setPreferredSize(new Dimension(120, 40));
        loadGameBtn.addActionListener(e -> onLoadGame());
        chipPanel.add(loadGameBtn);

        JButton replayBtn = new JButton("Replay");
        replayBtn.setPreferredSize(new Dimension(120, 40));
        replayBtn.addActionListener(e -> onReplayClicked());
        chipPanel.add(replayBtn);

        bottomPanel.add(chipPanel, BorderLayout.CENTER);

        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorLabel.setForeground(Color.RED);
        errorLabel.setPreferredSize(new Dimension(800, 30));
        bottomPanel.add(errorLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        render(controller.getViewState());
        setVisible(true);
    }

    private void onLoadGame() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                GameStorage storage = new FileGameStorage(file.getAbsolutePath());
                GameSaver saver = new GameSaver(storage);

                saver.loadGame(game);

                render(controller.getViewState());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to load game: " + ex.getMessage(),
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Color getColorForChip(ChipColor color) {
        switch (color) {
            case RED:
                return new Color(255, 100, 100);
            case BLUE:
                return new Color(100, 150, 255);
            case GREEN:
                return new Color(100, 255, 100);
            case BLACK:
                return new Color(80, 80, 80);
            case WHITE:
                return new Color(240, 240, 240);
            default:
                return Color.GRAY;
        }
    }

    public void render(GameViewState state) {
        currentPlayerLabel.setText("Current Player: " + (state.getCurrentPlayer() + 1));

        player1Label.setText("Player 1 - VP: " + state.getP1VP() + " | Chips: " + formatChips(state.getP1Chips()));
        player2Label.setText("Player 2 - VP: " + state.getP2VP() + " | Chips: " + formatChips(state.getP2Chips()));

        cardsPanel.removeAll();
        for (Card card : state.getCards()) {
            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            cardPanel.setBackground(Color.WHITE);

            JLabel idLabel = new JLabel("Card " + card.getId());
            idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(idLabel);

            JLabel costLabel = new JLabel("Cost: " + card.getCostString());
            costLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(costLabel);

            JLabel vpLabel = new JLabel("VP: " + card.getVictoryPoints());
            vpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(vpLabel);

            JButton buyBtn = new JButton("Buy");
            buyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            buyBtn.addActionListener(e -> onCardClicked(card.getId()));
            cardPanel.add(buyBtn);

            cardsPanel.add(cardPanel);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();

        showError(state.getErrorMessage());
    }

    private String formatChips(Map<ChipColor, Integer> chips) {
        StringBuilder sb = new StringBuilder();
        for (ChipColor color : ChipColor.values()) {
            int count = chips.getOrDefault(color, 0);
            if (count > 0) {
                sb.append(color.toString()).append(count).append(" ");
            }
        }
        return sb.toString().trim();
    }

    public void showError(String message) {
        if (message != null && !message.isEmpty()) {
            errorLabel.setText(message);
        } else {
            errorLabel.setText(" ");
        }
    }

    public void onCardClicked(int cardId) {
        controller.buyCard(cardId);
        render(controller.getViewState());
    }

    public void onChipClicked(ChipColor color) {
        controller.takeChipIncremental(color);
        render(controller.getViewState());
    }

    public void onNewGameClicked() {
        controller.newGame();
        render(controller.getViewState());
    }

    public void onReplayClicked() {
        ReplayController replayController = new ReplayController(game);
        new ReplayUI(replayController);
    }
}
