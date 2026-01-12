package domain;

import java.util.List;
import java.util.ArrayList;

public class PlayerMove {
    private final MoveType type;
    private final int cardId;
    private final List<ChipColor> colors;
    
    public PlayerMove(MoveType type, int cardId, List<ChipColor> colors) {
        this.type = type;
        this.cardId = cardId;
        this.colors = new ArrayList<>(colors);
    }
    
    public MoveType getType() {
        return type;
    }
    
    public int getCardId() {
        return cardId;
    }
    
    public List<ChipColor> getColors() {
        return new ArrayList<>(colors);
    }
}
