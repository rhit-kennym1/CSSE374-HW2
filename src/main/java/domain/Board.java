package domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Board {
    private final List<Card> availableCards;

    public Board(List<Card> cards) {
        this.availableCards = new ArrayList<>(cards);
    }

    public Board() {
        this.availableCards = new ArrayList<>();
        initializeDefaultCards();
    }

    private void initializeDefaultCards() {
        availableCards.add(createCard(1, 3, 0, 0, 0, 0, 1));
        availableCards.add(createCard(2, 0, 3, 0, 0, 0, 1));
        availableCards.add(createCard(3, 0, 0, 3, 0, 0, 1));
        availableCards.add(createCard(4, 0, 0, 0, 3, 0, 1));
        availableCards.add(createCard(5, 0, 0, 0, 0, 3, 1));

        availableCards.add(createCard(6, 2, 2, 0, 0, 0, 2));
        availableCards.add(createCard(7, 0, 2, 2, 0, 0, 2));
        availableCards.add(createCard(8, 0, 0, 2, 2, 0, 2));
        availableCards.add(createCard(9, 0, 0, 0, 2, 2, 2));
        availableCards.add(createCard(10, 2, 0, 0, 0, 2, 2));

        availableCards.add(createCard(11, 2, 1, 1, 0, 0, 3));
        availableCards.add(createCard(12, 0, 2, 1, 1, 0, 3));
        availableCards.add(createCard(13, 0, 0, 2, 1, 1, 3));

        availableCards.add(createCard(14, 3, 2, 1, 0, 0, 4));
        availableCards.add(createCard(15, 3, 3, 3, 0, 0, 5));
    }

    private Card createCard(int id, int r, int b, int g, int bl, int w, int vp) {
        Map<ChipColor, Integer> cost = new HashMap<>();
        if (r > 0)
            cost.put(ChipColor.RED, r);
        if (b > 0)
            cost.put(ChipColor.BLUE, b);
        if (g > 0)
            cost.put(ChipColor.GREEN, g);
        if (bl > 0)
            cost.put(ChipColor.BLACK, bl);
        if (w > 0)
            cost.put(ChipColor.WHITE, w);
        return new Card(id, cost, vp);
    }

    public List<Card> getCards() {
        return availableCards;
    }

    public Card getCard(int cardId) throws IllegalMoveException {
        for (int i = 0; i < availableCards.size(); i++) {
            if (availableCards.get(i).getId() == cardId) {
                return availableCards.get(i);
            }
        }
        throw new IllegalMoveException("Card not found on board");
    }

    public Card removeCard(int cardId) throws IllegalMoveException {
        for (int i = 0; i < availableCards.size(); i++) {
            if (availableCards.get(i).getId() == cardId) {
                return availableCards.remove(i);
            }
        }
        throw new IllegalMoveException("Card not found on board");
    }

    public void reset() {
        availableCards.clear();
        initializeDefaultCards();
    }
}
