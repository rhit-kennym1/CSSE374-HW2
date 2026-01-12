package domain;

import java.util.List;
import java.util.ArrayList;

public class Game {
    private Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private final List<PlayerMove> moves;
    private final GameSaver saver;
    private final List<ChipColor> currentTurnChips;
    private String lastError;

    public Game(Board board, List<Player> players, GameSaver saver) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.moves = new ArrayList<>();
        this.saver = saver;
        this.currentTurnChips = new ArrayList<>();
        this.lastError = null;
    }

    public List<PlayerMove> getMoves() {
        return new ArrayList<>(moves);
    }

    public void buyCard(int cardId) throws IllegalMoveException {
        if (!currentTurnChips.isEmpty()) {
            throw new IllegalMoveException("Cannot buy card after taking chips this turn");
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        Card card = board.getCard(cardId);

        if (!currentPlayer.canAfford(card)) {
            throw new IllegalMoveException("Not enough chips to buy this card");
        }

        card = board.removeCard(cardId);

        currentPlayer.payCost(card.getCost());
        currentPlayer.addVictoryPoints(card.getVictoryPoints());

        moves.add(new PlayerMove(MoveType.BUY_CARD, cardId, new ArrayList<>()));
        nextPlayer();
        save();
        lastError = null;
    }

    public void takeTwo(ChipColor color) throws IllegalMoveException {
        if (currentTurnChips.size() > 0) {
            throw new IllegalMoveException("Already took chips this turn");
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.takeChip(color, 2);
        currentTurnChips.add(color);
        currentTurnChips.add(color);

        List<ChipColor> moveColors = new ArrayList<>();
        moveColors.add(color);
        moves.add(new PlayerMove(MoveType.TAKE_TWO_SAME, -1, moveColors));

        nextPlayer();
        save();
        lastError = null;
    }

    public void takeThree(ChipColor c1, ChipColor c2, ChipColor c3) throws IllegalMoveException {
        if (c1 == c2 || c2 == c3 || c1 == c3) {
            throw new IllegalMoveException("All three colors must be different");
        }

        if (currentTurnChips.size() > 0) {
            throw new IllegalMoveException("Already took chips this turn");
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.takeChip(c1, 1);
        currentPlayer.takeChip(c2, 1);
        currentPlayer.takeChip(c3, 1);

        List<ChipColor> moveColors = new ArrayList<>();
        moveColors.add(c1);
        moveColors.add(c2);
        moveColors.add(c3);
        moves.add(new PlayerMove(MoveType.TAKE_THREE_DIFF, -1, moveColors));

        nextPlayer();
        save();
        lastError = null;
    }

    public void takeChipIncremental(ChipColor color) throws IllegalMoveException {
        if (currentTurnChips.isEmpty()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            currentPlayer.takeChip(color, 1);
            currentTurnChips.add(color);
            lastError = null;
        } else if (currentTurnChips.size() == 1) {
            Player currentPlayer = players.get(currentPlayerIndex);

            if (currentTurnChips.get(0) == color) {
                currentPlayer.takeChip(color, 1);
                currentTurnChips.add(color);

                List<ChipColor> moveColors = new ArrayList<>();
                moveColors.add(color);
                moves.add(new PlayerMove(MoveType.TAKE_TWO_SAME, -1, moveColors));
                nextPlayer();
                save();
                lastError = null;
            } else {
                currentPlayer.takeChip(color, 1);
                currentTurnChips.add(color);
                lastError = null;
            }
        } else if (currentTurnChips.size() == 2) {
            if (currentTurnChips.contains(color)) {
                throw new IllegalMoveException("Third chip must be a different color than the first two");
            }

            Player currentPlayer = players.get(currentPlayerIndex);
            currentPlayer.takeChip(color, 1);

            List<ChipColor> moveColors = new ArrayList<>();
            moveColors.add(currentTurnChips.get(0));
            moveColors.add(currentTurnChips.get(1));
            moveColors.add(color);
            moves.add(new PlayerMove(MoveType.TAKE_THREE_DIFF, -1, moveColors));

            nextPlayer();
            save();
            lastError = null;
        }
    }

    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentTurnChips.clear();
    }

    public void startNewGame() {
        saver.backupSave();
        board.reset();
        for (Player player : players) {
            player.reset();
        }
        currentPlayerIndex = 0;
        moves.clear();
        currentTurnChips.clear();
        lastError = null;
        save();
    }

    public void save() {
        if (saver != null) {
            saver.saveGame(this);
        }
    }

    public void loadIfExists() {
        if (saver != null) {
            saver.loadGame(this);
        }
    }

    public GameViewState getViewState() {
        Player p1 = players.get(0);
        Player p2 = players.get(1);

        return new GameViewState(
                currentPlayerIndex,
                board.getCards(),
                p1.getChips(),
                p2.getChips(),
                p1.getVictoryPoints(),
                p2.getVictoryPoints(),
                lastError,
                new ArrayList<>(currentTurnChips));
    }

    public void setLastError(String error) {
        this.lastError = error;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public List<ChipColor> getCurrentTurnChips() {
        return new ArrayList<>(currentTurnChips);
    }

    public void setState(Board board, int currentPlayerIndex, List<ChipColor> turnChips) {
        this.board = board;
        this.currentPlayerIndex = currentPlayerIndex;
        this.currentTurnChips.clear();
        this.currentTurnChips.addAll(turnChips);
    }

    public void addMove(PlayerMove move) {
        this.moves.add(move);
    }
}
