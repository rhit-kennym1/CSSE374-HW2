package domain;

import java.util.Map;
import java.util.HashMap;

public class Card {
    private final int id;
    private final Map<ChipColor, Integer> cost;
    private final int victoryPoints;
    
    public Card(int id, Map<ChipColor, Integer> cost, int victoryPoints) {
        this.id = id;
        this.cost = new HashMap<>(cost);
        this.victoryPoints = victoryPoints;
    }
    
    public int getId() {
        return id;
    }
    
    public Map<ChipColor, Integer> getCost() {
        return new HashMap<>(cost);
    }
    
    public int getVictoryPoints() {
        return victoryPoints;
    }
    
    public String getCostString() {
        StringBuilder sb = new StringBuilder();
        for (ChipColor color : ChipColor.values()) {
            int count = cost.getOrDefault(color, 0);
            if (count > 0) {
                sb.append(color.toString()).append(count);
            }
        }
        return sb.toString();
    }
}
