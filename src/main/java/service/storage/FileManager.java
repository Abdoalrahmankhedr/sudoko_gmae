package service.storage;

import exceptions.FileNotCreatedException;

import java.io.*;
import java.util.Scanner;

/* Andrew :) */
public class FileManager {
    private final String filePath;
    private final File file;

    public FileManager(String filePath) throws FileNotCreatedException {
        this.filePath = filePath;
        this.file = new File(filePath);
        create();
    }

    /* Delete file */
    public void delete() {
        if (this.file.exists()) {
            if (this.file.delete()) {
                System.out.printf("[FileManager] Successfully deleted file '%s'\n", this.filePath);
            } else {
                System.out.printf("[FileManager] Failed to delete file '%s', attempting to truncate instead...\n",
                        this.filePath);
                write(""); // Fallback: Overwrite with empty content to ensure log is cleared
            }
        }
    }

    /* Create new file in path given */
    /*
     * Used to ensure that a file exists before proceeding to complete an action
     */
    public void create() {
        if (!this.file.exists()) {
            try {
                if (!this.file.createNewFile()) {
                    throw new FileNotCreatedException("Couldn't create file: " + this.filePath);
                }
            } catch (IOException err) {
                System.out.printf("[FileManager] Attempted to create file '%s', this error occurred: %s \n",
                        this.filePath, err.getMessage());
            }
        }
    }

    /* Get File Content */
    public String read() {
        create();
        try (Scanner scanner = new Scanner(this.file)) {
            StringBuilder sb = new StringBuilder();

            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n");
            }

            return sb.toString();
        } catch (FileNotFoundException err) {
            /* I highly doubt this error will ever print if file didn't exist */
            System.out.printf("[FileManager] File '%s' does not exist\n", this.filePath);
        }

        return null;
    }

    /* Overwrite file content */
    public void write(String content) {
        create();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath))) {
            writer.write(content);
        } catch (IOException err) {
            System.out.printf("[FileManager] Error occurred on writing file '%s'\n", this.filePath);
        }
    }

    /* Append one line at the end */
    public void append(String line) {
        create();
        try (PrintWriter appender = new PrintWriter(new FileWriter(this.filePath, true))) {
            appender.print(line);
        } catch (IOException err) {
            System.out.printf("[FileManager] Error occurred on appending file '%s'\n", this.filePath);
        }
    }
}
