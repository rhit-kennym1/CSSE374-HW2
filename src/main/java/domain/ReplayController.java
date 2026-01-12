package domain;

import java.io.File;
import java.util.List;
import javax.swing.Timer;

import datasource.FileGameStorage;

public class ReplayController implements Controller {
    private final List<PlayerMove> moves;
    private int currentIndex;
    private Game replayGame;
    private final Game originalGame;
    private Timer replayTimer;
    private int speed;
    private boolean isPlaying;

    public ReplayController(Game game) {
        this.originalGame = game;
        this.moves = game.getMoves();
        this.currentIndex = 0;
        this.speed = 1000;
        this.isPlaying = false;
        resetReplayGame();
    }

    public static ReplayController fromFile(File file) throws Exception {
        FileGameStorage storage = new FileGameStorage(file.getAbsolutePath());
        GameSaver saver = new GameSaver(storage);

        Board board = new Board();
        Player p1 = new Player("Player 1", new HumanClickStrategy());
        Player p2 = new Player("Player 2", new HumanClickStrategy());
        Game game = new Game(board, java.util.List.of(p1, p2), saver);

        saver.loadGame(game);

        return new ReplayController(game);
    }

    private void resetReplayGame() {
        Board board = new Board();
        Player p1 = new Player("Player 1", new HumanClickStrategy());
        Player p2 = new Player("Player 2", new HumanClickStrategy());
        List<Player> players = new java.util.ArrayList<>();
        players.add(p1);
        players.add(p2);
        this.replayGame = new Game(board, players, null);
    }

    @Override
    public GameViewState getViewState() {
        return replayGame.getViewState();
    }

    public void play() {
        if (isPlaying)
            return;
        isPlaying = true;

        replayTimer = new Timer(speed, e -> {
            if (currentIndex < moves.size()) {
                stepForward();
            } else {
                pause();
            }
        });
        replayTimer.start();
    }

    public void changeSpeed(int speed) {
        this.speed = speed;
        if (replayTimer != null && isPlaying) {
            replayTimer.setDelay(speed);
        }
    }

    public void pause() {
        isPlaying = false;
        if (replayTimer != null) {
            replayTimer.stop();
        }
    }

    public void stepForward() {
        if (currentIndex < moves.size()) {
            PlayerMove move = moves.get(currentIndex);
            try {
                applyMove(move);
                currentIndex++;
            } catch (IllegalMoveException e) {
                e.printStackTrace();
            }
        }
    }

    public void stepBackward() {
        if (currentIndex > 0) {
            currentIndex--;
            resetReplayGame();
            for (int i = 0; i < currentIndex; i++) {
                try {
                    applyMove(moves.get(i));
                } catch (IllegalMoveException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void reset() {
        pause();
        currentIndex = 0;
        resetReplayGame();
    }

    private void applyMove(PlayerMove move) throws IllegalMoveException {
        switch (move.getType()) {
            case BUY_CARD:
                replayGame.buyCard(move.getCardId());
                break;
            case TAKE_TWO_SAME:
                replayGame.takeTwo(move.getColors().get(0));
                break;
            case TAKE_THREE_DIFF:
                List<ChipColor> colors = move.getColors();
                replayGame.takeThree(colors.get(0), colors.get(1), colors.get(2));
                break;
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalMoves() {
        return moves.size();
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
