package views;

import exceptions.GameInvalidException;
import exceptions.GameNotFoundException;
import exceptions.SolutionInvalidException;
import model.Catalog;
import model.UserAction;

import java.io.IOException;

public interface Controllable {
    Catalog getCatalog();
    int[][] getGame(char level) throws GameNotFoundException;
    void driveGames(int[][] source) throws SolutionInvalidException;
    boolean[][] verifyGame(int[][] game);
    int[][] solveGame(int[][] game) throws GameInvalidException;
    void logUserAction(UserAction userAction) throws IOException;
}
