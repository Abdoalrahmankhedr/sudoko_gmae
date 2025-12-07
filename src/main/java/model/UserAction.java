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

    /* Create a UserAction from a String format */
    public UserAction(String actionFormat) {
        String content = actionFormat.trim().substring(1, actionFormat.length() - 1);
        String[] numbers = content.split(",\\s*");

        if (numbers.length != 4) throw new IllegalArgumentException("Insufficient user action details");

        try {
            this.x = Integer.parseInt(numbers[0]);
            this.y = Integer.parseInt(numbers[1]);
            this.current = Integer.parseInt(numbers[2]);
            this.prev = Integer.parseInt(numbers[3]);

        } catch (NumberFormatException err) {
            throw new IllegalArgumentException("format does include non number characters");
        }
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
