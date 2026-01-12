package domain;

public enum ChipColor {
    RED, BLUE, GREEN, BLACK, WHITE;
    
    @Override
    public String toString() {
        return name().charAt(0) + "";
    }
}
