package com.erkspace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * A very simple Naive Bayes classifier implementation
 *
 * @author Erkut Evirgen
 * 
 * Your classified.txt is something like :
 * 
 * Red,Sports,Domestic,Yes
 * Yellow,Sports,Domestic,No
 * Yellow,Sports,Imported,Yes 
 * and so on...
 * 
 * And your training.txt is something like :
 * 
 * Red,Sports,Imported,[X]
 * Yellow,Sports,Domestic,[X]
 * and so on...
 * 
 * And you want to predict [X]'s by using Naive Bayes Probabilistic model : 
 * 
 * classify(feature1, ..., featureN) = argmax(P(category) * PROD(P(feature|category)))
 * 
 * 
 *
 */

public class NaiveBayes {
	
	int attrs = 0;
	
	ArrayList<String> classifiedData = new ArrayList<String>();
	ArrayList<String> trainingData = new ArrayList<String>();
	
	String[][] classify_values = null;
	String[][] training_values = null;
	
	int classifyInputRows = 0;
	int trainingInputRows = 0;
	
	HashMap<String, Integer> correctedValues = new HashMap<>();
	
	String[] predictions = null;	

	public static void main(String args[]) throws Exception  {
		NaiveBayes nb = new NaiveBayes();
		nb.loadClassifiedFile();
		nb.loadClassifyCasesToArray();
		nb.loadTrainingFile();
		nb.loadTrainingCasesToArray();
		nb.classifyValues();
		nb.writeTheResults();
	}
	
	/**
     * Basically reads the classified data
     * attrs : the column count, refers attributes
     * classify
     */
	public void loadClassifiedFile() throws IOException {		
		BufferedReader br = new BufferedReader(new FileReader("classified.txt"));
		String dataline;
		while ((dataline = br.readLine()) != null) {			
			classifiedData.add(dataline);			
		}		
		br.close();		
		attrs = NaiveBayesUtils.howManyCommas((String) classifiedData.get(0));	
		classifyInputRows = classifiedData.size();
		System.out.println("There is " + attrs + " categories ");
		System.out.println("There is " + classifyInputRows + " rows in classified ");	
	}

	/**
     * Basically reads the test data
     * attrs : the column count, refers attributes
     * predictions : refers the results, size is the row count of the file
     */
	public void loadTrainingFile() throws IOException {		
		BufferedReader br = new BufferedReader(new FileReader("training.txt"));
		String dataline;
		while ((dataline = br.readLine()) != null) {			
			trainingData.add(dataline);			
		}		
		br.close();			
		trainingInputRows = trainingData.size();
		predictions = new String[trainingData.size()];
		System.out.println("There is " + attrs + " categories ");
		System.out.println("There is " + trainingInputRows + " rows in training ");	
	}
	
	/**
     * Converts the classified data to a 2 dimension String array
     * correctedValues : is a hashmap saves the count of different results
     * predictions : refers the results, size is the row count of the file
     */	
	public void loadClassifyCasesToArray() {
		classifyInputRows = classifiedData.size();
		classify_values = new String[classifyInputRows][attrs+1];

		for (int r = 0; r < classifyInputRows; r++) {
			String s = (String) classifiedData.get(r);
			StringTokenizer tokens = new StringTokenizer(s, ",");
			for (int a = 0; a <= attrs; a++) {
				String value = tokens.nextToken();
				classify_values[r][a] = value;
				if (a==attrs) {
					correctedValues = NaiveBayesUtils.increaseTheCorrectedValues(correctedValues, value);
					System.out.println("That's it : " + value);
					
				}
			}			
		}
		// writes the arrays to the console. that's all..
		NaiveBayesUtils.printArrayToConsole(classify_values);
		NaiveBayesUtils.printHashMapToConsole(correctedValues);
	}

	/**
     * Converts the training data to a 2 dimension String array
     *  
     */	
	public void loadTrainingCasesToArray() {
		trainingInputRows = trainingData.size();
		training_values = new String[trainingInputRows][attrs];

		for (int r = 0; r < trainingInputRows; r++) {
			String s = (String) trainingData.get(r);
			StringTokenizer tokens = new StringTokenizer(s, ",");
			for (int a = 0; a < attrs; a++) {
				String value = tokens.nextToken();
				training_values[r][a] = value;
			}			
		}
		NaiveBayesUtils.printArrayToConsole(training_values);
	}

	/*
	 * Classifies and the make predictions for the training.txt
	 * 
	 */
	public void classifyValues() {
		//First of all we create Set<String> to iterate our correct results as grouped		
		Set<String> keys = correctedValues.keySet();
		
		//This is our buffer to use to find maxArgs by correct result
		HashMap<String, Double> prediction = new HashMap<>();

		//Journey begins.. We are iterating the test file row by row..
		for (int r = 0; r < trainingInputRows; r++) {
			
			// I created this to find max value related to the count in the classified file.
			double myRowValue[] = new double[attrs];
			
			//Drain my "prediction" key by row
			for (String key : keys) {
				prediction.put(key, 0d);
			}
			System.out.println("Training iteration for row: " + r + "");
			
			//I am checking my corrected values one by one
			for (String key : keys) {
				//And I am looking my columns by the key (in attached example (Yes, No and Maybe) 
				for (int a = 0; a < attrs; a++) {					
					System.out.println("it is going to check " + key + " for " + classify_values[r][a] );
					// Checking column for the key. And calculate how many times it appears 
					myRowValue[a] = predict(classify_values[r][a], key, a);
				}
				double genericTotalRatio = 0;
				//we need to multiply prediction and the (key count / total key count) 
				genericTotalRatio = ((double)correctedValues.get(key) / classifyInputRows) ;
				
				//and we put in prediction. we will find the maxArgs later.
				prediction.put(key, (multiplyValuesOfArray(myRowValue) * genericTotalRatio ));
			}
			//Okay, we are starting another loop to find maximum result
			double maxValue = 0;
			String maxResult = "";
			Set<String> predictionKeys = prediction.keySet();
			for (String key : predictionKeys) {
				if (prediction.get(key)> maxValue) {
					maxValue = prediction.get(key);
					maxResult = key;					
				}
			}
			
			System.out.println("ROW " + (r+1) + " and the value = [" + maxResult + "]");
			
			//we found it. to show later, we put the result in our prediction array.
			predictions[r] = maxResult;
			
		} // and next row please
		
	}
	
	/*
	 * This function multiplies every value of a double array each other 
	 */
	public double multiplyValuesOfArray(double myRowValue[]) {
		double result = 1;
		for(double dbl : myRowValue){
            result = result * dbl;
        }
		return result;		
	}
	
	/*
	 * Founds how many keys with a value occurs. 
	 * @param column is only for the column count. The prediction is only for the last column of the file.
	 */
	public double predict(String checkValue, String key, int column) {
		
		double count = 0;
		// If it finds the same classified result for the key in the file count ++
		for (int row = 0; row < classify_values.length; row++) {
			if (classify_values[row][column].equals(checkValue) && key.equals(classify_values[row][attrs])) {
				count++;
			}
		}
		System.out.println("Classifield file includes " + count + " times " + key + " value for " +  checkValue + "." );
		// And this is the part of the Naive Bayes, divide founded result with the total key count.
		double result = count / correctedValues.get(key);
		
		//this is only a by-pass. If no result found, whole Naive Bayes formula returns zero because of multiply by zero
		if (result == 0) result = 0.6; //0.6 is only a number. nothing more than a number. We don't want to return zero.. :) 
		return result;
	}
	
	// just to show it on the console.
	public void writeTheResults() {
		
		for (int row = 0; row < trainingData.size(); row++) {
			System.out.println( trainingData.get(row) + ": " + predictions[row]  );
		}
		
	}

}
