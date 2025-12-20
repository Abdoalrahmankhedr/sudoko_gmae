package service.catalogue;

import model.Catalog;
import model.Difficulty;

import java.io.File;

public class GameCatalogue {
    private static GameCatalogue instance;
    private final String baseDir = "src/main/java/resources/";

    private GameCatalogue() {}

    public static GameCatalogue getInstance() {
        if (instance == null) {
            instance = new GameCatalogue();
        }
        return instance;
    }

    public Catalog getCatalog() {
        boolean hasCurrent = checkCurrentGame();
        boolean hasAllModes = checkAllDifficulties();

        return new Catalog(hasCurrent, hasAllModes);
    }


    private boolean checkCurrentGame() {
        File incompleteGame = new File(baseDir + "incomplete/game.csv");
        return incompleteGame.exists() && incompleteGame.isFile();
    }

    private boolean checkAllDifficulties() {
        for (Difficulty difficulty : Difficulty.values()) {
            String dirPath = baseDir + difficulty.name().toLowerCase();
            File dir = new File(dirPath);

            if (!dir.exists() || !dir.isDirectory()) {
                return false;
            }

            File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
            if (files == null || files.length == 0) {
                return false;
            }
        }

        return true;
    }

    public boolean hasGamesForDifficulty(Difficulty difficulty) {
        String dirPath = baseDir + difficulty.name().toLowerCase();
        File dir = new File(dirPath);

        if (!dir.exists() || !dir.isDirectory()) {
            return false;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
        return files != null && files.length > 0;
    }
}

