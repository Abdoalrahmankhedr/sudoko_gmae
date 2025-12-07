package service.log;

import model.UserAction;

/* Andrew :) */
/*
    Uses the idea of an Adapter to connect between String & UserAction
*/
public class UserActionLogger {
    /* Add a new log entry */
    public static void record(UserAction action) {
        FileLogger.record(action.toString() + "\n");
    }

    /* Remove the latest log entry */
    public static UserAction removeLast() {
        String last = FileLogger.removeLast();
        if (last != null) return new UserAction(last);
        else return null;
    }

    /* Delete log file */
    public static void delete() {
        FileLogger.delete();
    }
}
