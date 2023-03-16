package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.util.*;


import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {
    private Map<String, Integer> trainSpamFreq; // Map that maps words to their frequency in the spam training data.
    private Map<String, Integer> trainHamFreq;
    public SpamDetector(String spamFile, String hamFile) { // default constructor
        this.trainSpamFreq = new HashMap<>();
        this.trainHamFreq = new HashMap<>();
        loadFrequencyMap(spamFile, trainSpamFreq);
        loadFrequencyMap(hamFile, trainHamFreq);
    }
    //private double accuracy;
    //private double precision;
    //private int totalTestFiles = 0;
    //private int truePositives = 0;
    //private int falsePositives = 0;
    //private int trueNegatives = 0;
    //private int falseNegatives = 0;
    // Load frequency maps from files


    public boolean isValidWord(String word) {
        String validCharacters = "^[a-zA-Z]+$";
        // Returns true if and only if word contains valid characters.
        if (word.matches(validCharacters)) {
            return true;
        } else {
            return false;
        }
    }

    private Map<String, Integer> readFile(File file) throws IOException { // helper function to parse the file
        Map<String, Integer> wordCounts = new TreeMap<>();
        if (file.isDirectory()) {
            File[] content = file.listFiles();
            for (File current : content) {
                wordCounts.putAll(readFile(current));
            }
        } else {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String token = scanner.next();
                if (isValidWord(token)) {
                    if (wordCounts.containsKey(token)) {
                        int previous = wordCounts.get(token);
                        wordCounts.put(token, previous + 1);
                    } else {
                        wordCounts.put(token, 1);
                    }
                }
            }
            scanner.close();
        }
        return wordCounts;
    }
    private void loadFrequencyMap(String fileName, Map<String, Integer> wordCounts) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) { // Use a try-with-resources block to automatically close the BufferedReader after
            String line = reader.readLine();
            while (line != null) { // loop each line
                String[] parts = line.split(": ");
                String word = parts[0];
                int count = Integer.parseInt(parts[1]);
                wordCounts.put(word, count); // append the word with the frequency to the map
                line = reader.readLine(); // read next line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void countWord(String word) {
        List<String> arr = new ArrayList<>(); // Create an empty ArrayList to keep track of the words already counted
        if (arr.contains(word)) { // if word already in, exit
            return;
        }
        if (wordCounts.containsKey(word)) { // If the word has already been counted before, increment its count
            int previous = wordCounts.get(word);
            wordCounts.put(word, previous + 1);
            arr.add(word); // append the word to the list of counted words
        } else { // If the word has not been counted before, add it to the map with a count of 1
            wordCounts.put(word, 1);
            arr.add(word); // Add the word to the list of counted words
        }
    }

    public double accuracy(List<String> actual, List<String> predicted) {
        if (actual.size() != predicted.size()) {
            throw new IllegalArgumentException("The size of actual and predicted lists must be equal.");
        }
        int correct = 0;
        for (int i = 0; i < actual.size(); i++) { // loop through each message in the test set
            if (actual.get(i).equals(predicted.get(i))) {
                correct++;
            }
        }
        return (double) correct / actual.size(); // calculate and return the accuracy
    }

    public double precision(List<String> actual, List<String> predicted, String targetClass) {
        int truePositives = 0;
        int falsePositives = 0;
        // Iterate through each element in the actual and predicted lists
        for (int i = 0; i < actual.size(); i++) {
            // If the predicted label matches the target class
            if (predicted.get(i).equals(targetClass)) {
                // If the actual label also matches the target class, increment true positives
                if (actual.get(i).equals(targetClass)) {
                    truePositives++;
                    // Otherwise, increment false positives
                } else {
                    falsePositives++;
                }
            }
        }
        // If there are no true positives or false positives, return 0.0 to avoid divide by zero error
        if (truePositives + falsePositives == 0) {
            return 0.0;
            // Otherwise, calculate the precision as the ratio of true positives to the sum of true positives and false positives
        } else {
            return (double) truePositives / (truePositives + falsePositives);
        }
    }
    public List<String> listTestFiles() {
        List<String> testFiles = new ArrayList<>();
        File testFolder = new File(TEST_DIR);
        if (testFolder.exists() && testFolder.isDirectory()) {
            File[] files = testFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        testFiles.add(file.getName());
                    }
                }
            }
        }
        return testFiles;
    }
}
    private double getSpamProbability(File file) {
        double probability = 0.5;  // initialize probability to given 0.5
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] words = line.split(" ");
                for (String word : words) { // loop for each word in the array
                    if (trainSpamFreq.containsKey(word)) {
                        double wordProbability = (double) trainSpamFreq.get(word) / (double) totalSpamWords; // probability calculation
                        probability *= wordProbability;
                    } else {
                        probability *= unknownWordProb; // if word not in
                    }
                }
                line = reader.readLine(); // read next line
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return probability; // return overall probability
    }

    private double getHamProbability(File file) {
        double probability = 0.5;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    if (trainHamFreq.containsKey(word)) {
                        double wordProbability = (double) trainHamFreq.get(word) / (double) totalHamWords;
                        probability *= wordProbability;
                    } else {
                        probability *= unknownWordProb;
                    }
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return probability;
    }
    public List<TestFile> trainAndTest(File mainDirectory) {
        List<TestFile> testFiles = listTestFiles(mainDirectory);
        // Load training data from the given directory
        loadFrequencyMaps(new File(mainDirectory, "train-features.txt"));

        for (TestFile file : testFiles) {  // Loop through all test files
            double spamProbability = getSpamProbability(file.getFile());
            double hamProbability = getHamProbability(file.getFile());

            double spamProbRounded = Math.round(spamProbability * 100000.0) / 100000.0; // estimate up to 5 decimal places
            double hamProbRounded = Math.round(hamProbability * 100000.0) / 100000.0;

            if (spamProbability > hamProbability) {
                file.setSpamProbability(spamProbability);
                file.setHamProbability(hamProbability);
                file.setActualClass("Spam");
                file.setSpamProbRounded(spamProbRounded);
                file.setHamProbRounded(hamProbRounded);
            } else {
                file.setSpamProbability(spamProbability);
                file.setHamProbability(hamProbability);
                file.setActualClass("Ham");
                file.setSpamProbRounded(spamProbRounded);
                file.setHamProbRounded(hamProbRounded);
            }
        }

        return testFiles;
    }
