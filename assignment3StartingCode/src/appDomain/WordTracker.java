package appDomain;

import java.io.*;
import java.util.*;

import implementations.BSTree;

// Class to represent metadata for each word
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

    public int getTotalFrequency() {
        return occurrences.values().stream().mapToInt(List::size).sum();
    }

    public void merge(WordMetadata other) {
        for (Map.Entry<String, List<Integer>> entry : other.getOccurrences().entrySet()) {
            occurrences.putIfAbsent(entry.getKey(), new ArrayList<>());
            occurrences.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(word + ":\n");
        for (Map.Entry<String, List<Integer>> entry : occurrences.entrySet()) {
            sb.append("  File: ").append(entry.getKey()).append(" Lines: ").append(entry.getValue()).append("\n");
        }
        sb.append("  Total Frequency: ").append(getTotalFrequency()).append("\n");
        return sb.toString();
    }

    @Override
    public int compareTo(WordMetadata other) {
        return this.word.compareTo(other.word);
    }
}

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

        BSTree<WordMetadata> tree = loadRepository();
        processFile(tree, inputFile);
        saveRepository(tree);

        PrintStream out = outputFile != null ? new PrintStream(new FileOutputStream(outputFile)) : System.out;
        boolean includeLines = option.equals("-pl") || option.equals("-po");
        boolean includeFrequency = option.equals("-po");

        utilities.Iterator<WordMetadata> iterator = tree.inorderIterator();
        while (iterator.hasNext()) {
            WordMetadata metadata = iterator.next();
            out.println(formatMetadata(metadata, includeLines, includeFrequency));
        }

        if (outputFile != null) {
            out.close();
        }
    }

    private static void processFile(BSTree<WordMetadata> tree, String inputFile) throws IOException {
        // Remove any existing occurrences from the input file
        utilities.Iterator<WordMetadata> iterator = tree.inorderIterator();
        List<WordMetadata> toUpdate = new ArrayList<>();

        while (iterator.hasNext()) {
            WordMetadata metadata = iterator.next();
            if (metadata.getOccurrences().containsKey(inputFile)) {
                metadata.removeOccurrencesFromFile(inputFile);
                toUpdate.add(metadata);
            }
        }

        // Reprocess the file and update the tree
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Normalize the line by replacing "it's" with "its" and handling other cases
                String normalizedLine = line.replaceAll("\\bit's\\b", "its");

                String[] words = normalizedLine.split("\\W+"); // Split by non-word characters
                for (String word : words) {
                    if (!word.isEmpty()) {
                        String normalizedWord = word.toLowerCase();

                        // Handle special cases where "it's" becomes "its" directly
                        if (normalizedWord.equals("it's")) {
                            normalizedWord = "its";
                        }

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
        utilities.Iterator<WordMetadata> iterator = tree.inorderIterator();
        while (iterator.hasNext()) {
            WordMetadata metadata = iterator.next();
            if (metadata.getWord().equals(word)) {
                return metadata;
            }
        }
        return null;
    }

    private static String formatMetadata(WordMetadata metadata, boolean includeLines, boolean includeFrequency) {
        StringBuilder sb = new StringBuilder(metadata.getWord() + ":\n");
        for (Map.Entry<String, List<Integer>> entry : metadata.getOccurrences().entrySet()) {
            sb.append("  File: ").append(entry.getKey());
            if (includeLines) {
                sb.append(" Lines: ").append(entry.getValue());
            }
        }
        if (includeFrequency) {
            sb.append("  Total Frequency: ").append(metadata.getTotalFrequency());
        }
        sb.append("\n");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static BSTree<WordMetadata> loadRepository() {
        File file = new File(REPOSITORY_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (BSTree<WordMetadata>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new BSTree<>();
    }

    private static void saveRepository(BSTree<WordMetadata> tree) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REPOSITORY_FILE))) {
            oos.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}