package com.adamiworks.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

	/**
	 * Remove an element from array.
	 * 
	 * @param array
	 * @param element
	 * @param occurrences
	 *            0 for all or the maximum number of occurences to remove.
	 * @return
	 */
	public static String[] removeElement(String array[], String element,
			int occurrences) {

		List<String> list = new ArrayList<String>();

		if (array.length == 1) {
			return new String[0];
		}

		int removed = 0;

		for (String s : array) {
			if (s.trim().toUpperCase().equals(element.trim().toUpperCase())) {
				removed++;

				if (removed <= occurrences) {
					continue;
				}
			}

			list.add(s);
		}

		String newArray[] = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			newArray[i] = list.get(i);
		}

		return newArray;
	}
}
