package appDomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a word and its entries across multiple files and line numbers.
 * 
 * <p>
 * Each word is associated with a map where the key is the file name and the
 * value is a list of line numbers indicating where the word occurs.
 * </p>
 */
class WordInfo implements Serializable, Comparable<WordInfo> {

	private static final long serialVersionUID = 1L;

	private String word;
	private Map<String, List<Integer>> entries;

	/**
	 * Constructs a WordInfo object for a specific word.
	 * 
	 * @param word The word.
	 */
	public WordInfo(String word) {
		this.word = word;
		this.entries = new HashMap<>();
	}

	/**
	 * Retrieves the word associated with this WordInfo.
	 * 
	 * @return The word as a string.
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Retrieves the entries map for this word.
	 * 
	 * @return A map where keys are file names and values are lists of line numbers.
	 */
	public Map<String, List<Integer>> getEntries() {
		return entries;
	}

	/**
	 * Adds an entry of the word in a specific file and line number.
	 * 
	 * @param fileName   The name of the file where the word occurs.
	 * @param lineNumber The line number where the word occurs.
	 */
	public void addEntries(String fileName, int lineNumber) {
		entries.putIfAbsent(fileName, new ArrayList<>());
		entries.get(fileName).add(lineNumber);
	}

	/**
	 * Removes all entries of the word from a specific file.
	 * 
	 * @param fileName The name of the file to remove entries from.
	 */
	public void removeEntriesFromFile(String fileName) {
		entries.remove(fileName);
	}

	/**
	 * Calculates the total number of entries of the word across all files.
	 * 
	 * @return The total number of entries.
	 */
	public int getTotal() {
		return entries.values().stream().mapToInt(List::size).sum();
	}

	/**
	 * Merges the info from another Word object into this one.
	 * 
	 * @param other The other Word object to merge from.
	 */
	public void merge(WordInfo other) {
		for (Map.Entry<String, List<Integer>> entry : other.getEntries().entrySet()) {
			entries.putIfAbsent(entry.getKey(), new ArrayList<>());
			entries.get(entry.getKey()).addAll(entry.getValue());
		}
	}

	/**
	 * Compares this WordInfo object to another based on the associated word.
	 * 
	 * @param other The other WordInfo object to compare to.
	 * @return A negative integer, zero, or a positive integer as this word is
	 *         lexicographically less than, equal to, or greater than the other
	 *         word.
	 */
	@Override
	public int compareTo(WordInfo other) {
		return this.word.compareTo(other.word);
	}
}
