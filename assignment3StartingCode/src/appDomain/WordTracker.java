package appDomain;

import java.io.*;
import java.util.*;
import implementations.BSTree;
import serialization.Serialization;
import utilities.Iterator;

/**
 * A WordTracker application that tracks and processes entries of words in text
 * files.
 *
 * <p>
 * This class uses a Binary Search Tree (BST) to store information about words,
 * including the files and line numbers where they occur. It supports commands
 * for processing input files, saving/loading data from a repository, and
 * outputting results.
 * </p>
 */
public class WordTracker {
	/**
	 * Entry point for the WordTracker application.
	 * 
	 * @param args Command-line arguments specifying input file, options, and output
	 *             file.
	 * @throws IOException            If an I/O error occurs.
	 * @throws ClassNotFoundException If the repository file cannot be deserialized.
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 2) {
			System.out.println("Usage: java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f <output.txt>]");
			return;
		}

		String baseDir = "res";
		String repositoryFile = baseDir + File.separator + "repository.ser";

		String inputFile = baseDir + File.separator + args[0];
		String option = args[1];
		String outputFile = (args.length == 4 && args[2].equals("-f")) ? baseDir + File.separator + args[3] : null;

		File repoFile = new File(repositoryFile);
		BSTree<WordInfo> tree;
		if (repoFile.exists()) {
			tree = Serialization.loadFromFile(repoFile);
		} else {
			System.out.println("Repository file not found. Creating a new repository.");
			tree = new BSTree<>();
		}

		processFile(tree, inputFile);
		Serialization.saveToFile(tree, repositoryFile);

		boolean hasLines = "-pl".equals(option) || "-po".equals(option);
		boolean hasTotal = "-po".equals(option);

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
			outputToFile(tree, fileStream, hasLines, hasTotal);
			outputToFile(tree, System.out, hasLines, hasTotal);
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
			outputToFile(tree, System.out, hasLines, hasTotal);
			System.out.println("\nNot exporting file");
		}

	}

	/**
	 * Processes the input file and updates the word information in the tree.
	 * 
	 * @param tree      The BST storing word information.
	 * @param inputFile The input file to process.
	 * @throws IOException If an error occurs while reading the file.
	 */
	private static void processFile(BSTree<WordInfo> tree, String inputFile) throws IOException {
		// Remove any existing entries from the input file
		Iterator<WordInfo> iterator = tree.inorderIterator();
		List<WordInfo> toUpdate = new ArrayList<>();

		while (iterator.hasNext()) {
			WordInfo info = iterator.next();
			if (info.getEntries().containsKey(inputFile)) {
				info.removeEntriesFromFile(inputFile);
				toUpdate.add(info);
			}
		}

		try (Scanner scanner = new Scanner(new File(inputFile))) {
			int lineNumber = 0;

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lineNumber++;

				String normalizedLine = line.replaceAll("'", "");

				String[] words = normalizedLine.split("\\W+"); //
				for (String word : words) {
					if (!word.isEmpty()) {
						String normalizedWord = word.toLowerCase();

						WordInfo newInfo = new WordInfo(normalizedWord);
						newInfo.addEntries(inputFile, lineNumber);

						WordInfo existingInfo = findInTree(tree, normalizedWord);
						if (existingInfo != null) {
							existingInfo.merge(newInfo);
						} else {
							tree.add(newInfo);
						}
					}
				}
			}
		}

	}

	/**
	 * Finds information for a specific word in the tree.
	 * 
	 * @param tree The BST storing word information.
	 * @param word The word to find.
	 * @return The corresponding WordInfo object, or null if not found.
	 */
	private static WordInfo findInTree(BSTree<WordInfo> tree, String word) {
		Iterator<WordInfo> iterator = tree.inorderIterator();
		while (iterator.hasNext()) {
			WordInfo info = iterator.next();
			if (info.getWord().equals(word)) {
				return info;
			}
		}
		return null;
	}

	/**
	 * Formats information for output.
	 * 
	 * @param info	The information to format.
	 * @param hasLines     	Whether to include line numbers.
	 * @param hasTotal 		Whether to include word counts.
	 * @return A formatted string representing the information.
	 */
	private static String formatOutput(WordInfo info, boolean hasLines, boolean hasTotal) {
		StringBuilder sb = new StringBuilder("Key : ===" + info.getWord() + "=== ");
		if (hasTotal) {
			sb.append("number of entries: ").append(info.getTotal());
		}

		for (Map.Entry<String, List<Integer>> entry : info.getEntries().entrySet()) {
			sb.append(" found in file: ").append(entry.getKey());
			if (hasLines) {
				String lineNumbers = entry.getValue().toString().replace("[", "").replace("]", "");
				sb.append(" on lines: ").append(lineNumbers).append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * Outputs word information to the specified PrintStream.
	 * 
	 * @param tree     The BST storing word information.
	 * @param out      The PrintStream to write output to.
	 * @param hasLines Whether to include line numbers in the output.
	 * @param hasTotal Whether to include total word count in the output.
	 */

	private static void outputToFile(BSTree<WordInfo> tree, PrintStream out, boolean hasLines, boolean hasTotal) {
		Iterator<WordInfo> iterator = tree.inorderIterator();
		while (iterator.hasNext()) {
			WordInfo info = iterator.next();
			out.println(formatOutput(info, hasLines, hasTotal));
		}
	}

}