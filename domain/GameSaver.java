package domain;

import datasource.FileGameStorage;
import datasource.GameStorage;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class GameSaver {
    private final GameStorage storage;

    public GameSaver(GameStorage storage) {
        this.storage = storage;
    }

    public void backupSave() {
        storage.backup();
    }

    public void saveGame(Game game) {
        StringBuilder sb = new StringBuilder();

        sb.append(game.getCurrentPlayerIndex()).append("\n");

        for (Player player : game.getPlayers()) {
            sb.append(player.getName()).append("|");
            sb.append(player.getVictoryPoints()).append("|");
            for (ChipColor color : ChipColor.values()) {
                sb.append(color.name()).append(":").append(player.getChips().get(color)).append(",");
            }
            sb.append("\n");
        }

        for (Card card : game.getBoard().getCards()) {
            sb.append(card.getId()).append("|");
            sb.append(card.getVictoryPoints()).append("|");
            for (Map.Entry<ChipColor, Integer> entry : card.getCost().entrySet()) {
                sb.append(entry.getKey().name()).append(":").append(entry.getValue()).append(",");
            }
            sb.append("\n");
        }

        sb.append("CHIPS\n");
        for (ChipColor color : game.getCurrentTurnChips()) {
            sb.append(color.name()).append(",");
        }
        sb.append("\n");

        sb.append("MOVES\n");
        System.out.println(game.getMoves().size());
        for (PlayerMove move : game.getMoves()) {
            sb.append(move.getType().name()).append("|");
            sb.append(move.getCardId()).append("|");
            for (ChipColor color : move.getColors()) {
                sb.append(color.name()).append(",");
            }
            sb.append("\n");
        }

        storage.save(sb.toString());
    }

    public void loadGame(Game game) {
        if (!storage.exists()) {
            return;
        }

        try {
            String data = storage.load();
            String[] lines = data.split("\n");

            int lineIndex = 0;
            int currentPlayerIndex = Integer.parseInt(lines[lineIndex++]);

            for (int i = 0; i < 2; i++) {
                String[] playerData = lines[lineIndex++].split("\\|");
                Player player = game.getPlayers().get(i);
                player.reset();

                int vp = Integer.parseInt(playerData[1]);
                for (int v = 0; v < vp; v++) {
                    player.addVictoryPoints(1);
                }

                String[] chips = playerData[2].split(",");
                for (String chip : chips) {
                    if (!chip.isEmpty()) {
                        String[] parts = chip.split(":");
                        ChipColor color = ChipColor.valueOf(parts[0]);
                        int count = Integer.parseInt(parts[1]);
                        player.takeChip(color, count);
                    }
                }
            }

            Board newBoard = new Board();
            newBoard.getCards().clear();

            while (lineIndex < lines.length && !lines[lineIndex].equals("CHIPS")) {
                String[] cardData = lines[lineIndex++].split("\\|");
                int id = Integer.parseInt(cardData[0]);
                int vp = Integer.parseInt(cardData[1]);

                Map<ChipColor, Integer> cost = new java.util.HashMap<>();
                String[] costData = cardData[2].split(",");
                for (String c : costData) {
                    if (!c.isEmpty()) {
                        String[] parts = c.split(":");
                        cost.put(ChipColor.valueOf(parts[0]), Integer.parseInt(parts[1]));
                    }
                }

                newBoard.getCards().add(new Card(id, cost, vp));
            }

            lineIndex++;
            java.util.List<ChipColor> turnChips = new java.util.ArrayList<>();
            if (lineIndex < lines.length && !lines[lineIndex].isEmpty() && !lines[lineIndex].equals("MOVES")) {
                String[] chipData = lines[lineIndex].split(",");
                for (String chip : chipData) {
                    if (!chip.isEmpty()) {
                        turnChips.add(ChipColor.valueOf(chip));
                    }
                }
                lineIndex++;
            }

            lineIndex++;

            if (lineIndex < lines.length && lines[lineIndex].equals("MOVES")) {
                lineIndex++;
                while (lineIndex < lines.length && !lines[lineIndex].isEmpty()) {
                    String[] moveData = lines[lineIndex++].split("\\|");
                    MoveType type = MoveType.valueOf(moveData[0]);
                    int cardId = Integer.parseInt(moveData[1]);

                    List<ChipColor> colors = new ArrayList<>();
                    if (moveData.length > 2 && !moveData[2].isEmpty()) {
                        String[] colorData = moveData[2].split(",");
                        for (String c : colorData) {
                            if (!c.isEmpty()) {
                                colors.add(ChipColor.valueOf(c));
                            }
                        }
                    }

                    game.addMove(new PlayerMove(type, cardId, colors));
                }
            }

            game.setState(newBoard, currentPlayerIndex, turnChips);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
