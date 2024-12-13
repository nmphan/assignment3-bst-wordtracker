package appDomain;

import java.io.*;
import java.util.*;
import implementations.BSTree;
import serialization.Serialization;
import utilities.Iterator;

public class WordTracker implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 245030236857842948L;
	private static final String REPOSITORY_FILE = "repository.ser";

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 2) {
			System.out.println("Usage: java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f <output.txt>]");
			return;
		}
		
		// Base directory for file paths
	    String baseDir = "res";
	    String repositoryFile = baseDir + File.separator + "repository.ser";

	    String inputFile = baseDir + File.separator + args[0];
		String option = args[1];
		String outputFile = (args.length == 4 && args[2].equals("-f")) ? baseDir + File.separator + args[3] : null;
		
		// Load or create repository
		File repoFile = new File(repositoryFile);
	    BSTree<WordMetadata> tree;
	    if (repoFile.exists()) {
	        tree = Serialization.loadFromFile(repoFile);
	    } else {
	        System.out.println("Repository file not found. Creating a new repository.");
	        tree = new BSTree<>();
	    }

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
			String fileName = entry.getKey().replaceFirst("^res[\\\\/]", "");
			sb.append(" found in file: ").append(fileName);
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