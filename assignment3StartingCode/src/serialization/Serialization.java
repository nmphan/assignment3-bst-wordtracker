package serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import implementations.BSTree;

//Adapted from: geeksforgeeks
//Source: https://www.geeksforgeeks.org/serialization-in-java


public class Serialization {
	/**
     * Saves the given BSTree to a file using object serialization.
     * 
     * @param <E>      The type of elements in the BSTree.
     * @param t        The BSTree to be saved.
     * @param fileName The name of the file to save the BSTree to.
     */
	public static <E extends Comparable<? super E>> void saveToFile(BSTree<E> t,String fileName) {
        try {
        	FileOutputStream file = new FileOutputStream(fileName);
        	@SuppressWarnings("resource")
			ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(t);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
	
	/**
	 * Loads a BSTree from the specified file using object deserialization.
	 * 
	 * @param <E>      The type of elements in the BSTree.
	 * @param fileName The file containing the serialized BSTree.
	 * @return The deserialized BSTree object, or a new empty BSTree if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Comparable<? super E>> BSTree<E> loadFromFile(File fileName) {
        try {
        	FileInputStream file = new FileInputStream(fileName);
        	@SuppressWarnings("resource")
			ObjectInputStream in = new ObjectInputStream(file);
        	return (BSTree<E>) in.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("Error" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error" + e.getMessage());
        }
        return new BSTree<>();
    }
}
