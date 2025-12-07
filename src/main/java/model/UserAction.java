package model;

/* Andrew :) */
public class UserAction {
    private final int x;
    private final int y;
    private final int prev;
    private final int current;

    public UserAction(int x, int y, int prev, int current) {
        this.x = x;
        this.y = y;
        this.prev = prev;
        this.current = current;
    }

    /* For logging */
    @Override
    public String toString() {
        return String.format(
                "(%d, %d, %d, %d)",
                this.x, this.y, this.current, this.prev
        );
    }

    /* Getters */
    public int getX() {return x;}
    public int getY() {return y;}
    public int getPrev() {return prev;}
    public int getCurrent() {return current;}
}
