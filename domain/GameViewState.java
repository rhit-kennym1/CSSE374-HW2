package domain;

import java.util.Map;
import java.util.List;

public class GameViewState {
    private final int currentPlayer;
    private final List<Card> cards;
    private final Map<ChipColor, Integer> p1Chips;
    private final Map<ChipColor, Integer> p2Chips;
    private final int p1VP;
    private final int p2VP;
    private final String errorMessage;
    private final List<ChipColor> currentTurnChips;
    
    public GameViewState(int currentPlayer, List<Card> cards, 
                        Map<ChipColor, Integer> p1Chips, Map<ChipColor, Integer> p2Chips,
                        int p1VP, int p2VP, String errorMessage, List<ChipColor> currentTurnChips) {
        this.currentPlayer = currentPlayer;
        this.cards = cards;
        this.p1Chips = p1Chips;
        this.p2Chips = p2Chips;
        this.p1VP = p1VP;
        this.p2VP = p2VP;
        this.errorMessage = errorMessage;
        this.currentTurnChips = currentTurnChips;
    }
    
    public int getCurrentPlayer() { return currentPlayer; }
    public List<Card> getCards() { return cards; }
    public Map<ChipColor, Integer> getP1Chips() { return p1Chips; }
    public Map<ChipColor, Integer> getP2Chips() { return p2Chips; }
    public int getP1VP() { return p1VP; }
    public int getP2VP() { return p2VP; }
    public String getErrorMessage() { return errorMessage; }
    public List<ChipColor> getCurrentTurnChips() { return currentTurnChips; }
}
