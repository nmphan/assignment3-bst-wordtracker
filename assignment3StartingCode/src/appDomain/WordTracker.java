package appDomain;

import java.io.*;
import java.util.*;
import implementations.BSTree;
import serialization.Serialization;
import utilities.Iterator;

	/**
	 * A WordTracker application that tracks and processes occurrences of words in text files.
	 *
	 * <p>
	 * This class uses a Binary Search Tree (BST) to store metadata about words, including
	 * the files and line numbers where they occur. It supports commands for processing
	 * input files, saving/loading data from a repository, and outputting results.
	 * </p>
	 */
public class WordTracker implements Serializable {
	
	
	private static final long serialVersionUID = 245030236857842948L;
	private static final String REPOSITORY_FILE = "repository.ser";

	/**
     * Entry point for the WordTracker application.
     * 
     * @param args Command-line arguments specifying input file, options, and output file.
     * @throws IOException If an I/O error occurs.
     * @throws ClassNotFoundException If the repository file cannot be deserialized.
     */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 2) {
			System.out.println("Usage: java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f <output.txt>]");
			return;
		}

		String inputFile = "res/" + args[0];
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
			if (option.equals("-pf")) {
				System.out.println("Writing pf format");
			}
			if (option.equals("-pl")) {
				System.out.println("Writing pl format");
			}
			if (option.equals("-po")) {
				System.out.println("Writing po format");
			}
			outputMetadataToFile(tree, fileStream, hasLines, hasTotal);
			outputMetadataToFile(tree, System.out, hasLines, hasTotal);
			System.out.println("\nExporting file to: " + outputFile);
		} else {
			if (option.equals("-pf")) {
				System.out.println("Writing pf format");
			}
			if (option.equals("-pl")) {
				System.out.println("Writing pl format");
			}
			if (option.equals("-po")) {
				System.out.println("Writing po format");
			}
			outputMetadataToFile(tree, System.out, hasLines, hasTotal);
			System.out.println("\nNot exporting file");
		}

	}

	/**
     * Processes the input file and updates the word metadata in the tree.
     * 
     * @param tree      The BST storing word metadata.
     * @param inputFile The input file to process.
     * @throws IOException If an error occurs while reading the file.
     */
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
	            normalizedLine = normalizedLine.replaceAll("[^a-zA-Z0-9\\s]", "");

				String[] words = normalizedLine.split("\\s+");
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

	/**
     * Finds metadata for a specific word in the tree.
     * 
     * @param tree The BST storing word metadata.
     * @param word The word to find.
     * @return The corresponding WordMetadata object, or null if not found.
     */
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

	/**
     * Formats metadata for output.
     * 
     * @param metadata       The metadata to format.
     * @param includeLines   Whether to include line numbers.
     * @param includeFrequency Whether to include frequency counts.
     * @return A formatted string representing the metadata.
     */
	private static String formatMetadata(WordMetadata metadata, boolean includeLines, boolean includeFrequency) {		
		StringBuilder sb = new StringBuilder("Key : ===" + metadata.getWord() + "=== ");
		if (includeFrequency) {
			sb.append("number of entries: ").append(metadata.getTotal());
		}

		for (Map.Entry<String, List<Integer>> entry : metadata.getOccurrences().entrySet()) {
			sb.append(" found in file: ").append(entry.getKey().replace("res/", ""));
			if (includeLines) {
				String lineNumbers = entry.getValue().toString().replace("[", "").replace("]", "");
				sb.append(" on lines: ").append(lineNumbers).append(",");
			}
		}
		return sb.toString();
	}

	/**
     * Outputs word metadata to the specified PrintStream.
     * 
     * @param tree      The BST storing word metadata.
     * @param out       The PrintStream to write output to.
     * @param hasLines  Whether to include line numbers in the output.
     * @param hasTotal  Whether to include total occurrences in the output.
     */
	private static void outputMetadataToFile(BSTree<WordMetadata> tree, PrintStream out, boolean hasLines,
			boolean hasTotal) {		
		Iterator<WordMetadata> iterator = tree.inorderIterator();
		while (iterator.hasNext()) {
			WordMetadata metadata = iterator.next();
			out.println(formatMetadata(metadata, hasLines, hasTotal));
		}
		
	}
}
