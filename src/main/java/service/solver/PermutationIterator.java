package service.solver;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator Pattern implementation for generating permutations on-the-fly.
 * Generates all possible combinations of values for empty cells without storing them in memory.
 * This prevents memory overflow when dealing with 9^5 = 59,049 possible permutations.
 */
public class PermutationIterator implements Iterator<int[]> {
    private final int numPositions;
    private final int[] current;
    private boolean hasNext;


    public PermutationIterator(int numPositions) {
        if (numPositions < 0) {
            throw new IllegalArgumentException("Number of positions must be non-negative");
        }

        this.numPositions = numPositions;
        this.current = new int[numPositions];

        // Initialize all positions to 1 (first permutation)
        for (int i = 0; i < numPositions; i++) {
            current[i] = 1;
        }

        this.hasNext = numPositions > 0;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public int[] next() {
        if (!hasNext) {
            throw new NoSuchElementException("No more permutations available");
        }

        // Return copy of current permutation
        int[] result = current.clone();

        // Generate next permutation (like counting in base-9)
        int position = numPositions - 1;

        while (position >= 0) {
            if (current[position] < 9) {
                current[position]++;
                break;
            } else {
                current[position] = 1;
                position--;
            }
        }

        // If we've exhausted all positions, no more permutations
        if (position < 0) {
            hasNext = false;
        }

        return result;
    }

    /**
     * Resets the iterator to the beginning.
     */
    public void reset() {
        for (int i = 0; i < numPositions; i++) {
            current[i] = 1;
        }
        hasNext = numPositions > 0;
    }

    /**
     * Returns the total number of permutations (9^n).
     */
    public long getTotalPermutations() {
        return (long) Math.pow(9, numPositions);
    }
}