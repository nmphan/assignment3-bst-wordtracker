package appDomain;

import java.io.File;

import implementations.BSTree;
import serialization.Serialization;

public class WordTracker {

    public static void main(String[] args) {
    	File file = new File("res/repository.ser");
    	
    	try {
    		if (file.exists()) {
    			BSTree<String> wordTree = Serialization.loadFromFile(file);
    		}

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
}

