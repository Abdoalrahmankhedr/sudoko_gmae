package model;

public enum Difficulty {
    EASY('E'),
    MEDIUM('M'),
    HARD('H');

    private final char code;

    Difficulty(char code) {
        this.code = code;
    }

    /* Get enum from code */
    public static Difficulty fromChar(char c) {
        char upperCaseCode = Character.toUpperCase(c);

        // Iterate through all constants in the enum
        for (Difficulty difficulty : values()) {
            if (difficulty.getCode() == upperCaseCode) {
                return difficulty;
            }
        }

        throw new IllegalArgumentException("[Difficulty] invalid enum code: " + c);
    }

    /* Getters */
    public char getCode() {return this.code;}
}
