package controller;

import exceptions.GameInvalidException;
import exceptions.GameNotFoundException;
import exceptions.SolutionInvalidException;
import model.Catalog;
import model.Difficulty;
import model.Game;

import java.io.IOException;

public interface Viewable {
    Catalog getCatalog();
    Game getGame(Difficulty level) throws GameNotFoundException;
    void driveGames(Game source) throws SolutionInvalidException;
    String verifyGame(Game game);
    int[] solveGame(Game game) throws GameInvalidException;
    void logUserAction(String userAction) throws IOException;
}
