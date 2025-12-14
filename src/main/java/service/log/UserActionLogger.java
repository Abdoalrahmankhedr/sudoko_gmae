package service.log;

import model.UserAction;

/* Andrew :) */
/*
    Uses the idea of an Adapter to connect between String & UserAction
*/
public class UserActionLogger implements Logger<UserAction> {
    /* Default location to the log file, there is only one */
    private static final String logPath = "src/main/java/resources/incomplete/log.txt";
    /* Default location to the log file, there is only one */
    private final FileLogger logger = new FileLogger(logPath);
    /* Apply singleton pattern */
    private static UserActionLogger instance;

    private UserActionLogger() {}

    /* Apply singleton pattern */
    /* Make sure only one action logger is ever made in its lifecycle */
    public static UserActionLogger getInstance() {
        if (instance == null) {
            instance = new UserActionLogger();
        }
        return instance;
    }

    /* Add a new log entry */
    @Override
    public void record(UserAction action) {
        logger.record(action.toString() + "\n");
    }

    /* Remove the latest log entry */
    @Override
    public UserAction removeLast() {
        String last = logger.removeLast();
        if (last != null) return new UserAction(last);
        else return null;
    }

    /* Delete log file */
    @Override
    public void delete() {
        logger.delete();
    }
}
