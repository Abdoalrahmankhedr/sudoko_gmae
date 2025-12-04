//   Abdo -> works on start ,display and extractFirstNumber methods
// & Andrew -> works on runTasks method :)

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Executor { 
    /**
     * Validates a Sudoku board using different parallelization strategies.
     *
     * @param board 9x9 Sudoku grid to validate
     * @param mode Parallelization level:
     *             "0"  - Sequential (1 thread checks all rows, cols, boxes)
     *             "3"  - 3 parallel tasks (rows, cols, boxes separately)
     *             "27" - Maximum parallelism (27 threads, one per row/col/box)
     *
     * Prints "VALID" if board is correct, otherwise "INVALID" with detailed
     * error messages grouped by rows, columns, and boxes.
     */
    public static void start(int[][] board, String mode) {
        List<Callable<List<String>>> tasks = new ArrayList<>();
        int poolSize = 0;
        //this will switch the mode and build tasks based on the mode we on
        switch (mode){
            //this will execute rows ,columns then boxes in order
            case "0":
                tasks.add(() -> {
                    List<String> all = new ArrayList<>();
                    //rows
                    for (int i = 0; i < 9; i++) {
                        Worker w = new Worker(board, i, 'R');
                        List<String> res = w.call();
                        if (res != null && !res.isEmpty()) all.addAll(res);
                    }
                    // cols
                    for (int i = 0; i < 9; i++) {
                        Worker w = new Worker(board, i, 'C');
                        List<String> res = w.call();
                        if (res != null && !res.isEmpty()) all.addAll(res);
                    }
                    // boxes
                    for (int i = 0; i < 9; i++) {
                        Worker w = new Worker(board, i, 'B');
                        List<String> res = w.call();
                        if (res != null && !res.isEmpty()) all.addAll(res);
                    }
                    return all;
                });
                poolSize = 1;
                break;
            //this will execute rows ,columns then boxes in parallel
            case "3":
                //Rows
                tasks.add(() -> {
                    List<String> all = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        Worker w = new Worker(board, i, 'R');
                        List<String> res = w.call();
                        if (res != null && !res.isEmpty()) all.addAll(res);
                    }
                    return all;
                });
                //Columns
                tasks.add(() -> {
                    List<String> all = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        Worker w = new Worker(board, i, 'C');
                        List<String> res = w.call();
                        if (res != null && !res.isEmpty()) all.addAll(res);
                    }
                    return all;
                });
                //Boxes
                tasks.add(() -> {
                    List<String> all = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        Worker w = new Worker(board, i, 'B');
                        List<String> res = w.call();
                        if (res != null && !res.isEmpty()) all.addAll(res);
                    }
                    return all;
                });
                poolSize=3;
                break;
            //this will make task for each row,column and box to  run all in parallel
            case "27":
                for (int i = 0; i < 9; i++) {
                    final int idx = i;
                    tasks.add(() -> {
                        Worker w = new Worker(board, idx, 'R');
                        return w.call();
                    });
                }
                for (int i = 0; i < 9; i++) {
                    final int idx = i;
                    tasks.add(() -> {
                        Worker w = new Worker(board, idx, 'C');
                        return w.call();
                    });
                }
                for (int i = 0; i < 9; i++) {
                    final int idx = i;
                    tasks.add(() -> {
                        Worker w = new Worker(board, idx, 'B');
                        return w.call();
                    });
                }
                poolSize = 27;
                break;
            default:
                System.err.println("Unknown mode: " + mode + ". You Must USE 0, 3, or 27.");
                return;
        }
        List<String> results = runTasks(tasks, poolSize);

        if (results == null || results.isEmpty()) {
            System.out.println("VALID");
            return;
        }
        // Else -> IInvalid and we want Rows first, then separator, Cols, separator, Boxes.
        else{
            List<String> rows = new ArrayList<>();
            List<String> cols = new ArrayList<>();
            List<String> boxes = new ArrayList<>();

            for (String s : results) {
                if (s == null) continue;
                String message = s.trim().toUpperCase();
                if (message.startsWith("ROW")) rows.add(s);
                else if (message.startsWith("COL")) cols.add(s);
                else if (message.startsWith("BOX")) boxes.add(s);
            }

            //sort each group by the index number inside the string (extract first integer)
            Comparator<String> byIndex = (a, b) -> {
                int ia = extractFirstNumber(a);
                int ib = extractFirstNumber(b);
                return Integer.compare(ia, ib);
            };
            //sort the messages according to the indexes of the rows,columns and Boxes
            Collections.sort(rows, byIndex);
            Collections.sort(cols, byIndex);
            Collections.sort(boxes, byIndex);

            // Print the messages of the invalid messages in order
            System.out.println("INVALID");
            display(rows);
            System.out.println("------------------------------------------");
            display(cols);
            System.out.println("------------------------------------------");
            display(boxes);
        }

    }
    /**
     * this method iterates on the sentence and return the number of row,column or box that have a problem
     * this method is used to arrange the messages of the IInvalid sentences
     * and returns -1 if there is any problem
     **/
    private static int extractFirstNumber(String s) {
        if (s == null) return -1;
        StringBuilder num = new StringBuilder();
        boolean found = false;
        //this loop will take the first group of digits appears in the sentence
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                num.append(c);
                found = true;
            } else if (found) {
                break;
            }
        }
        //this try and catch block to catch the NumberFormatException if the num value does`t have a int
        try {
            return !num.isEmpty() ? Integer.parseInt(num.toString()) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * This method is used to display list of the messages of InVALID instead of repeated it
     */
    private static void display(List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String m : messages) {
            System.out.println(m);
        }
    }
    // Andrew :)
    /*
        For context, this function is expecting a task list created using Callable interface and expects it to return
        List<String>

        Reason being in the requirements, it was requested to print out invalids for all rows, columns and blocks.
        Meaning a task run may have multiple INVALIDS, therefore it will expect a possibility of multiple results.

        e.g. of a task:
        public class Task implements Callable<List<String>> {
            public Task() { ... }

            @Override
            public List<String> call() {
                // .. code here ..
            }
        }

        Each task will be run by one thread.
        If you have more tasks than threads, ExecutorService will queue tasks until a thread frees.
    */
    private static List<String> runTasks(List<Callable<List<String>>> tasks, int poolSize) {
        /* Create an ExecutorService resource with a specific thread pool size */
        try (ExecutorService executor = Executors.newFixedThreadPool(poolSize)) {
            /* It receives Callable tasks that run a list of string after thread execution */
            List<Future<List<String>>> futures = executor.invokeAll(tasks);
            List<String> all = new ArrayList<>();
            for (Future<List<String>> future : futures) {
                /* Wait for thread to return result */
                List<String> violations = future.get();
                if (violations != null && !violations.isEmpty()) all.addAll(violations);
            }
            /* Return results */
            return all;
        } catch (Exception err) {
            Thread.currentThread().interrupt();
            System.err.println("Execution interrupted: " + err.getMessage());
        }

        return new ArrayList<>();
    }
}
