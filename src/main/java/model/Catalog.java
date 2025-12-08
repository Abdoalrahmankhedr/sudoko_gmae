package model;

/* Andrew :) */
public class Catalog {
    private final boolean current;
    private final boolean allModesExist;

    public Catalog(boolean current, boolean allModesExist) {
        this.current = current;
        this.allModesExist = allModesExist;
    }

    /* Getters */
    public boolean isCurrent() {return current;}
    public boolean isAllModesExist() {return allModesExist;}
}
