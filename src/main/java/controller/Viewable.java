package controller;

import exceptions.GameInvalidException;
import exceptions.GameNotFoundException;
import exceptions.SolutionInvalidException;
import model.Catalog;
import model.Difficulty;
import model.Game;

import java.io.IOException;

public interface Viewable {
    /*
        There should be a GameCatalogue class which would return
        a Catalog model with the correct values.
    */
    Catalog getCatalog();

    /*
        Use the GameManager here to simply load the game by difficulty enum
    */
    Game getGame(Difficulty level) throws GameNotFoundException;

    /*
        Use the GameGenerator to generate the three difficulties, use the GameManager in composition
        to save the boards to file.
    */
    void driveGames(Game source) throws SolutionInvalidException;

    /*
        Use the Verifier to obtain the duplicate locations and then format it
    */
    String verifyGame(Game game);

    /*
        This should use the game Solver to solve the board, then only return the
        an array of the index followed by the answer to the missing cell in that index
        [1,2,10,8,12,4] -> Cell 1 has 2, Cell 10 has 8, Cell 12 has 4
                        -> {0,0} has 2, {1,0} has 8, {1,2} has 4
    */
    int[] solveGame(Game game) throws GameInvalidException;

    /*
        Simply use new UserAction(userAction) to get an entry.
        Then call:
            UserActionLogger.record(new UserAction(userAction));
    */
    void logUserAction(String userAction) throws IOException;
}
