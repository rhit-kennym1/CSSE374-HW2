package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ReplayUI extends JFrame {
    private ReplayController controller;
    private JButton loadFileBtn;
    private JPanel cardsPanel;
    private JLabel player1Label;
    private JLabel player2Label;
    private JLabel currentPlayerLabel;
    private JLabel moveCountLabel;
    private JButton playPauseBtn;
    private JButton stepBackBtn;
    private JButton stepForwardBtn;
    private JButton resetBtn;
    private JSlider speedSlider;

    public ReplayUI(ReplayController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Game Replay");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 700);

        JPanel topPanel = new JPanel(new GridLayout(4, 1));
        currentPlayerLabel = new JLabel("Current Player: 1", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(currentPlayerLabel);

        moveCountLabel = new JLabel("Move: 0 / " + controller.getTotalMoves(), SwingConstants.CENTER);
        moveCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(moveCountLabel);

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

        JPanel controlPanel = new JPanel(new FlowLayout());

        loadFileBtn = new JButton("Load File");
        loadFileBtn.setPreferredSize(new Dimension(120, 40));
        loadFileBtn.addActionListener(e -> onLoadFile());
        controlPanel.add(loadFileBtn);

        resetBtn = new JButton("Reset");
        resetBtn.setPreferredSize(new Dimension(100, 40));
        resetBtn.addActionListener(e -> onReset());
        controlPanel.add(resetBtn);

        stepBackBtn = new JButton("< Step Back");
        stepBackBtn.setPreferredSize(new Dimension(120, 40));
        stepBackBtn.addActionListener(e -> onStepBack());
        controlPanel.add(stepBackBtn);

        playPauseBtn = new JButton("Play");
        playPauseBtn.setPreferredSize(new Dimension(100, 40));
        playPauseBtn.addActionListener(e -> onPlayPause());
        controlPanel.add(playPauseBtn);

        stepForwardBtn = new JButton("Step Forward >");
        stepForwardBtn.setPreferredSize(new Dimension(130, 40));
        stepForwardBtn.addActionListener(e -> onStepForward());
        controlPanel.add(stepForwardBtn);

        JLabel speedLabel = new JLabel("Speed:");
        controlPanel.add(speedLabel);

        speedSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, 1000);
        speedSlider.setPreferredSize(new Dimension(200, 40));
        speedSlider.addChangeListener(e -> onSpeedChange());
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setPaintTicks(true);
        controlPanel.add(speedSlider);

        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        render(controller.getViewState());
        setVisible(true);

        javax.swing.Timer updateTimer = new javax.swing.Timer(100, e -> {
            if (controller.isPlaying()) {
                render(controller.getViewState());
                updatePlayButton();
            }
        });
        updateTimer.start();
    }

    private void onLoadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try {
                ReplayController newController = ReplayController.fromFile(file);
                this.controller.pause(); // stop any existing replay
                controller = newController;
                render(newController.getViewState());
                moveCountLabel.setText("Move: 0 / " + newController.getTotalMoves());
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
        moveCountLabel.setText("Move: " + controller.getCurrentIndex() + " / " + controller.getTotalMoves());

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

            cardsPanel.add(cardPanel);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
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

    private void onPlayPause() {
        if (controller.isPlaying()) {
            controller.pause();
            playPauseBtn.setText("Play");
        } else {
            controller.play();
            playPauseBtn.setText("Pause");
        }
    }

    private void updatePlayButton() {
        if (controller.isPlaying()) {
            playPauseBtn.setText("Pause");
        } else {
            playPauseBtn.setText("Play");
        }

        if (controller.getCurrentIndex() >= controller.getTotalMoves()) {
            playPauseBtn.setText("Play");
        }
    }

    private void onStepForward() {
        controller.pause();
        controller.stepForward();
        render(controller.getViewState());
        updatePlayButton();
    }

    private void onStepBack() {
        controller.pause();
        controller.stepBackward();
        render(controller.getViewState());
        updatePlayButton();
    }

    private void onReset() {
        controller.reset();
        render(controller.getViewState());
        updatePlayButton();
    }

    private void onSpeedChange() {
        int speed = speedSlider.getValue();
        controller.changeSpeed(speed);
    }
}
