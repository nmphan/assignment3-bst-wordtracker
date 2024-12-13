package appDomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents metadata for a word, including its occurrences across multiple files and line numbers.
 * 
 * <p>
 * Each word is associated with a map where the key is the file name and the value
 * is a list of line numbers indicating where the word occurs.
 * </p>
 */
class WordMetadata implements Serializable, Comparable<WordMetadata> {

    private static final long serialVersionUID = 1L;

    private String word;
    private Map<String, List<Integer>> occurrences; // FileName -> List of Line Numbers

    /**
     * Constructs a WordMetadata object for a specific word.
     * 
     * @param word The word to track metadata for.
     */
    public WordMetadata(String word) {
        this.word = word;
        this.occurrences = new HashMap<>();
    }

    /**
     * Adds an occurrence of the word in a specific file and line number.
     * 
     * @param fileName   The name of the file where the word occurs.
     * @param lineNumber The line number where the word occurs.
     */
    public void addOccurrence(String fileName, int lineNumber) {
        occurrences.putIfAbsent(fileName, new ArrayList<>());
        occurrences.get(fileName).add(lineNumber);
    }

    /**
     * Removes all occurrences of the word from a specific file.
     * 
     * @param fileName The name of the file to remove occurrences from.
     */
    public void removeOccurrencesFromFile(String fileName) {
        occurrences.remove(fileName);
    }

    /**
     * Retrieves the word associated with this metadata.
     * 
     * @return The word as a string.
     */
    public String getWord() {
        return word;
    }

    /**
     * Retrieves the occurrences map for this word.
     * 
     * @return A map where keys are file names and values are lists of line numbers.
     */
    public Map<String, List<Integer>> getOccurrences() {
        return occurrences;
    }

    /**
     * Calculates the total number of occurrences of the word across all files.
     * 
     * @return The total number of occurrences.
     */
    public int getTotal() {
        return occurrences.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Merges the metadata from another WordMetadata object into this one.
     * 
     * @param other The other WordMetadata object to merge from.
     */
    public void merge(WordMetadata other) {
        for (Map.Entry<String, List<Integer>> entry : other.getOccurrences().entrySet()) {
            occurrences.putIfAbsent(entry.getKey(), new ArrayList<>());
            occurrences.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    /**
     * Compares this WordMetadata object to another based on the associated word.
     * 
     * @param other The other WordMetadata object to compare to.
     * @return A negative integer, zero, or a positive integer as this word is
     *         lexicographically less than, equal to, or greater than the other word.
     */
    @Override
    public int compareTo(WordMetadata other) {
        return this.word.compareTo(other.word);
    }
}
