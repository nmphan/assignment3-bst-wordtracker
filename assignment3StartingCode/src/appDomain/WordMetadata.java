package appDomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Class to represent metadata for each word
class WordMetadata implements Serializable, Comparable<WordMetadata> {
	private static final long serialVersionUID = 1L;
	private String word;
	private Map<String, List<Integer>> occurrences; // FileName -> List of Line Numbers

	public WordMetadata(String word) {
		this.word = word;
		this.occurrences = new HashMap<>();
	}

	public void addOccurrence(String fileName, int lineNumber) {
		occurrences.putIfAbsent(fileName, new ArrayList<>());
		occurrences.get(fileName).add(lineNumber);
	}

	public void removeOccurrencesFromFile(String fileName) {
		occurrences.remove(fileName);
	}

	public String getWord() {
		return word;
	}

	public Map<String, List<Integer>> getOccurrences() {
		return occurrences;
	}

	public int getTotal() {
		return occurrences.values().stream().mapToInt(List::size).sum();
	}

	public void merge(WordMetadata other) {
		for (Map.Entry<String, List<Integer>> entry : other.getOccurrences().entrySet()) {
			occurrences.putIfAbsent(entry.getKey(), new ArrayList<>());
			occurrences.get(entry.getKey()).addAll(entry.getValue());
		}
	}

	@Override
	public int compareTo(WordMetadata other) {
		return this.word.compareTo(other.word);
	}
}