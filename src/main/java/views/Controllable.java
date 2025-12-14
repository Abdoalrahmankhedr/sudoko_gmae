package views;

import exceptions.GameInvalidException;
import exceptions.GameNotFoundException;
import exceptions.SolutionInvalidException;
import model.Catalog;
import model.UserAction;

import java.io.IOException;

/*
    A class implementing this interface would have a Viewable dependency
    Therefore, these methods will adapt the parameters to fit with the parameters of the dependency

    Andrew :)
*/
public interface Controllable {
    /*
        There should be a GameCatalogue class which would return
        a Catalog model with the correct values.
    */
    Catalog getCatalog();

    /*
        use Difficulty.fromChar(level) to use in Viewable.getGame()
        then simply return the board in the Game model
    */
    int[][] getGame(char level) throws GameNotFoundException;

    /*
        Can use BoardManager.getFromFile() to get int[][] of board, convert that to Game model
        then use Viewable.driveGames()
    */
    void driveGames(String sourcePath) throws SolutionInvalidException;

    /*
        Convert the board to a Game model, then send to Viewable.verifyGame()
        you will receive a string where if valid -> bool[][] is all true
                                        if invalid -> extract duplicate locations, then add false
                                                      in these indexes on the board
    */
    boolean[][] verifyGame(int[][] game);

    /*
        Convert the board to a Game model, then send to Viewable.solveGame()
        you will receive the index followed by value as in example in Viewable.solveGame(),
            then map it to the same board and return that.
    */
    int[][] solveGame(int[][] game) throws GameInvalidException;

    /*
        Use Viewable.logUserAction()
    */
    void logUserAction(UserAction userAction) throws IOException;
}
