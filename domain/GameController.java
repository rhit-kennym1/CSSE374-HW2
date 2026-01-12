package domain;

public class GameController implements Controller {
    private final Game game;
    
    public GameController(Game game) {
        this.game = game;
    }
    
    @Override
    public GameViewState getViewState() {
        return game.getViewState();
    }
    
    public void buyCard(int cardId) {
        try {
            game.buyCard(cardId);
        } catch (IllegalMoveException e) {
            game.setLastError(e.getMessage());
        }
    }
    
    public void takeChipIncremental(ChipColor color) {
        try {
            game.takeChipIncremental(color);
        } catch (IllegalMoveException e) {
            game.setLastError(e.getMessage());
        }
    }
    
    public void newGame() {
        game.startNewGame();
    }
}
