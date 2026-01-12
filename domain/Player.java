package domain;

import java.util.Map;
import java.util.HashMap;

public class Player {
    private final String name;
    private final Map<ChipColor, Integer> chips;
    private int victoryPoints;
    private final PlayerStrategy strategy;
    
    public Player(String name, PlayerStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
        this.chips = new HashMap<>();
        for (ChipColor color : ChipColor.values()) {
            chips.put(color, 0);
        }
        this.victoryPoints = 0;
    }
    
    public String getName() {
        return name;
    }
    
    public Map<ChipColor, Integer> getChips() {
        return new HashMap<>(chips);
    }
    
    public int getVictoryPoints() {
        return victoryPoints;
    }
    
    public PlayerStrategy getStrategy() {
        return strategy;
    }
    
    public boolean canAfford(Card card) {
        Map<ChipColor, Integer> cost = card.getCost();
        for (Map.Entry<ChipColor, Integer> entry : cost.entrySet()) {
            if (chips.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    public void payCost(Map<ChipColor, Integer> cost) {
        for (Map.Entry<ChipColor, Integer> entry : cost.entrySet()) {
            chips.put(entry.getKey(), chips.get(entry.getKey()) - entry.getValue());
        }
    }
    
    public void takeChip(ChipColor color, int count) {
        chips.put(color, chips.get(color) + count);
    }
    
    public void addVictoryPoints(int points) {
        victoryPoints += points;
    }
    
    public void reset() {
        for (ChipColor color : ChipColor.values()) {
            chips.put(color, 0);
        }
        victoryPoints = 0;
    }
}
