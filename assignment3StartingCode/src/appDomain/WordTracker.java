package appDomain;

import java.io.*;
import java.util.*;
import implementations.BSTree;
import serialization.Serialization;
import utilities.Iterator;

public class WordTracker {
	private static final String REPOSITORY_FILE = "repository.ser";

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 2) {
			System.out.println("Usage: java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f <output.txt>]");
			return;
		}

		String inputFile = args[0];
		String option = args[1];
		String outputFile = args.length == 4 && args[2].equals("-f") ? args[3] : null;

		// Load or create repository
		BSTree<WordMetadata> tree = Serialization.loadFromFile(new File(REPOSITORY_FILE));

		// Process input file
		processFile(tree, inputFile);
		Serialization.saveToFile(tree, REPOSITORY_FILE);

		// Output results
		boolean hasLines = "-pl".equals(option) || "-po".equals(option);
		boolean hasTotal = "-po".equals(option);

		// Create a PrintStream to output to file if specified, otherwise use System.out
		// (console)
		PrintStream fileStream = outputFile != null ? new PrintStream(new FileOutputStream(outputFile)) : null;

		if (fileStream != null) {
			outputMetadataToFile(tree, fileStream, hasLines, hasTotal);
			outputMetadataToFile(tree, System.out, hasLines, hasTotal);
		} else {
			outputMetadataToFile(tree, System.out, hasLines, hasTotal);
		}

	}

	private static void processFile(BSTree<WordMetadata> tree, String inputFile) throws IOException {
		// Remove any existing occurrences from the input file
		Iterator<WordMetadata> iterator = tree.inorderIterator();
		List<WordMetadata> toUpdate = new ArrayList<>();

		while (iterator.hasNext()) {
			WordMetadata metadata = iterator.next();
			if (metadata.getOccurrences().containsKey(inputFile)) {
				metadata.removeOccurrencesFromFile(inputFile);
				toUpdate.add(metadata);
			}
		}

		// Reprocess the file and update the tree
		try (Scanner scanner = new Scanner(new File(inputFile))) {
			int lineNumber = 0;

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lineNumber++;

				String normalizedLine = line.replaceAll("'", "");

				String[] words = normalizedLine.split("\\W+"); // Split by non-word characters
				for (String word : words) {
					if (!word.isEmpty()) {
						String normalizedWord = word;

						WordMetadata newMetadata = new WordMetadata(normalizedWord);
						newMetadata.addOccurrence(inputFile, lineNumber);

						WordMetadata existingMetadata = findInTree(tree, normalizedWord);
						if (existingMetadata != null) {
							existingMetadata.merge(newMetadata);
						} else {
							tree.add(newMetadata);
						}
					}
				}
			}
		}

	}

	private static WordMetadata findInTree(BSTree<WordMetadata> tree, String word) {
		Iterator<WordMetadata> iterator = tree.inorderIterator();
		while (iterator.hasNext()) {
			WordMetadata metadata = iterator.next();
			if (metadata.getWord().equals(word)) {
				return metadata;
			}
		}
		return null;
	}

	private static String formatMetadata(WordMetadata metadata, boolean includeLines, boolean includeFrequency) {
		StringBuilder sb = new StringBuilder("Key : ===" + metadata.getWord() + "=== ");
		if (includeFrequency) {
			sb.append("number of entries: ").append(metadata.getTotal());
		}

		for (Map.Entry<String, List<Integer>> entry : metadata.getOccurrences().entrySet()) {
			sb.append(" found in file: ").append(entry.getKey());
			if (includeLines) {
				String lineNumbers = entry.getValue().toString().replace("[", "").replace("]", "");
				sb.append(" on lines: ").append(lineNumbers).append(",");
			}
		}
		return sb.toString();
	}

	private static void outputMetadataToFile(BSTree<WordMetadata> tree, PrintStream out, boolean hasLines,
			boolean hasTotal) {
		Iterator<WordMetadata> iterator = tree.inorderIterator();
		while (iterator.hasNext()) {
			WordMetadata metadata = iterator.next();
			out.println(formatMetadata(metadata, hasLines, hasTotal));
		}
	}

}