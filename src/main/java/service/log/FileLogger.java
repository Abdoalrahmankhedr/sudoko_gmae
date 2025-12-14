package service.log;

import service.storage.FileManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/* Andrew :) */
/*
    * IMPORTANT: Read FileManager for important detail
        -> On log file delete, it is recreated on content changing actions
*/
public class FileLogger implements Logger<String> {
    /* Only one manager is used to handle  */
    private final FileManager manager;

    public FileLogger(String logPath) {
        this.manager = new FileManager(logPath);
    }

    /* Add a new log entry */
    @Override
    public void record(String entry) {
        if (!entry.endsWith("\n")) manager.append(entry + "\n");
        else manager.append(entry);
    }

    /* Remove the latest log entry */
    @Override
    public String removeLast() {
        String content = manager.read();

        if (content == null || content.trim().isEmpty()) {
            System.out.println("[FileLogManager] Log file is currently empty, nothing is removed");
            return null;
        }

        /* Get logs and make sure it is not new lines */
        List<String> logs = Arrays.stream(content.split("\\n")).filter(log -> !log.isEmpty()).collect(Collectors.toList());
        if (logs.isEmpty()) {
            System.out.println("[FileLogManager] Log file is currently empty, nothing is removed");
            manager.write("");
            return null;
        }

        /* Log to console removed log entry */
        String removed = logs.removeLast();
        System.out.printf("[FileLogManager] (INFO) Removed log entry: %s\n", removed);

        /* Collect each log entry and separate them with a line separator '\n' */
        String editedContent = logs.stream().collect(Collectors.joining(System.lineSeparator()));

        manager.write(editedContent + "\n");
        return removed;
    }

    /* Delete log file */
    @Override
    public void delete() {
        manager.delete();
    }
}
