## INSTRUCTIONS FOR INSTALLING AND USING WORD TRACKER PROGRAM

***DESCRIPTION***
- This program includes an ADT for a BST, Iterators to be implemented as a BST.
- This program will read text files, collects and stores all the unique words it finds in those files.

***REQUIREMENTS***
- Java: JDK 8 or higher.
- A Java IDE like Eclipse (optional).
- Command-line Interface

***INSTALLATIONS***
- Download JDK 8 - java language, choosing the version compatible with your computer operations using this link: https://www.oracle.com/java/technologies/downloads/#java8

- Download the zipped project folder, and unzip it.

***USAGE***
- Open Command Prompt or Terminal
- Redirect to the location on your computer where the WordTracker.jar is located, for example:

		cd C:\Downloads\assignment3-bst-wordtracker/assignment3StartingCode

- Type the command line based on this format

		java -jar WordTracker.jar <input.txt> -pf/-pl/-po -f <output.txt>
  
  + <input.txt> is the path and filename of the text file to be processed by the WordTracker program.
  + There are three mutually exclusive options at the command line:
    - -pf prints in alphabetic order all words, along with the corresponding list of files in which the words occur.
    - -pl prints in alphabetic order all words, along with the corresponding list of files and line numbers in which the word occur.
    - -po prints in alphabetic order all words, along with the corresponding list of files, line numbers in which the word occur, and the frequency of occurrence of the words.
  + <output.txt> is an optional argument to redirect the report in the previous step to the path and filename specified in.

- The command at the beginning "java -jar WordTracker.jar" must be maintained unchanged.
- For the input.txt, it is open to take any txt file.
- Pay attention to spaces in the command, as incorrect spacing will cause errors.
