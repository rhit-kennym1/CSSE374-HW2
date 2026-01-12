package presentation;

import domain.*;
import datasource.*;
import java.util.*;

public class GameMain {
    private GameUI ui;
    private GameController controller;

    public static void main(String[] args) {
        new GameMain();
    }

    public GameMain() {
        GameStorage storage = new FileGameStorage("gamesave.txt");

        Player player1 = new Player("Player 1", new HumanClickStrategy());
        Player player2 = new Player("Player 2", new HumanClickStrategy());
        List<Player> players = Arrays.asList(player1, player2);

        Board board = new Board();
        GameSaver saver = new GameSaver(storage);

        Game game = new Game(board, players, saver);
        game.loadIfExists();

        controller = new GameController(game);
        ui = new GameUI(controller, game);
    }
}
