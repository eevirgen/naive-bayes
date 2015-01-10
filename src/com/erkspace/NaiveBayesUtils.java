package com.erkspace;

import java.util.HashMap;
import java.util.Set;

public class NaiveBayesUtils {

	/*
	 * Find a string's comma count
	 */
	public static int howManyCommas(String s) {
		int commas = 0;
		if (s != null) {
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == ',') {
					commas++;
				}
			}
		}
		return commas;
	}

	/*
	 * Writes a 2 dimension array values to the console
	 */
	public static void printArrayToConsole(String[][] input) {
		System.out.println("-----------------------");
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				System.out.print(input[i][j]);
				if (j < input[i].length - 1)
					System.out.print(",");
			}
			System.out.println();
		}
		System.out.println("-----------------------");
	}
	
	/*
	 * Writes a HashMap<String, Integer> values to the console 
	 */
	public static void printHashMapToConsole(HashMap<String, Integer> h) {
		Set<String> keys = h.keySet();
		System.out.println("-----------------------");
		// Loop over String keys.
		for (String key : keys) {
		    System.out.println(key + ":" + h.get(key));
		}
		System.out.println("-----------------------");
	}
	
	/*
	 * It puts values to the HashMap 
	 * To serve to use our HashMap<String, Integer> something like select count(*), value from myHashMap group by value"
	 *   
	 */
	public static HashMap<String, Integer> increaseTheCorrectedValues(HashMap<String, Integer> myHash , String myString) {
		int value = 0;
		if (!myHash.containsKey(myString)) {
			myHash.put(myString,1);
		} else {
			value = myHash.get(myString);
			myHash.put(myString, value+1);
		}
		return myHash;	
	}
	
	/*
	 * In case an emergency :)
	 */
	public static void main(String args[]) {
		System.out.println("Hello");
	}

}
