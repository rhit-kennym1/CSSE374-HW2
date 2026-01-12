package domain;

public interface PlayerStrategy {
    PlayerMove chooseMove(GameViewState state);
}
