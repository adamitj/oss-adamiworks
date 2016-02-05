package com.adamiworks.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {
	/**
	 * Turn the first letter of a String to uppercase
	 * 
	 * @param s
	 * @return
	 */
	public static String capitalize(String s) {
		StringBuilder sb = new StringBuilder(StringUtils.right(s, s.length() - 1));
		sb.insert(0, StringUtils.left(s, 1).toUpperCase());
		return sb.toString();
	}

	/**
	 * Retrieve the leftmost len chars
	 * 
	 * @param s
	 * @param len
	 * @return
	 */
	public static String left(String s, int len) {
		if (len > s.length()) {
			return s;
		}
		return s.substring(0, len);
	}

	/**
	 * Retrieve the rightmost len chars
	 * 
	 * @param s
	 * @param len
	 * @return
	 */
	public static String right(String s, int len) {
		if (len > s.length()) {
			return s;
		}
		return s.substring(s.length() - len, s.length());
	}

	/**
	 * Repeat a String n times
	 * 
	 * @param s
	 *            String to repeat
	 * @param n
	 *            times to repeat
	 * @return s *n times
	 */
	public static String repeat(String s, int n) {
		return new String(new char[n]).replace("\0", s);
	}

	/**
	 * Repeats a pattern at LEFT of a String and return n characters at right.
	 * Example:
	 * 
	 * <code>
	 * String var = StringUtils.padLeft("123", 6, "0");
	 * * var is equals to "000123"
	 * 
	 * String var = StringUtils.padLeft("111100", 6, "0");
	 * * var is equals to "111100"
	 * </code>
	 * 
	 * @param s
	 *            Base string
	 * @param n
	 *            Number of chars to be returned at RIGHT
	 * @param repeat
	 *            String to be repeated at LEFT
	 * @return
	 */
	public static String padLeft(String s, int n, String repeat) {
		StringBuilder sb = new StringBuilder(s);
		sb.insert(0, StringUtils.repeat(repeat, n));
		return StringUtils.right(sb.toString(), n);
	}

	/**
	 * Repeats a pattern at RIGHT of a String and return n characters at LEFT.
	 * Example:
	 * 
	 * <code>
	 * String var = StringUtils.padRight("123", 6, "0");
	 * * var is equals to "123000"
	 * 
	 * String var = StringUtils.padRight("111100", 6, "0");
	 * * var is equals to "111100"
	 * </code>
	 * 
	 * @param s
	 *            Base string
	 * @param n
	 *            Number of chars to be returned at LEFT
	 * @param repeat
	 *            String to be repeated at RIGHT
	 * @return
	 */
	public static String padRight(String s, int n, String repeat) {
		StringBuilder sb = new StringBuilder(s);
		sb.append(StringUtils.repeat(repeat, n));
		return StringUtils.left(sb.toString(), n);
	}

	/**
	 * Generates a MD5 Hash for the argument string.
	 * 
	 * @param arg
	 * @return
	 */
	public static String generateMD5Hash(String arg) {
		try {
			byte[] bytesOfMessage = arg.getBytes("UTF-8");

			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < thedigest.length; i++) {
				String hex = Integer.toHexString(0xff & thedigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getHashMD5(String arg) {
		return generateMD5Hash(arg);
	}

	/**
	 * Cut the # chars from the last of a String, including any character like
	 * spaces or tabs.
	 * 
	 * @param s
	 * @param cut
	 * @return
	 */
	public static String cutLast(String s, int cut) {
		return s.substring(0, s.length() - cut);
	}

	/**
	 * Cut the # chars from the begining of a String, including any character
	 * like spaces or tabs.
	 * 
	 * @param s
	 * @param cut
	 * @return
	 */
	public static String cutFirst(String s, int cut) {
		return s.substring(cut, s.length());
	}

	/**
	 * Calculates Levenshtein distance similarity of two given Strings.
	 * 
	 * @param s1
	 * @param s2
	 * @return a double from 0.0 to 1.0
	 */
	public static double similarity(String s1, String s2) {
		return StringSimilarity.similarity(s1, s2);
	}

	/**
	 * Calculates Levenshtein distance similarity of two given composite names.
	 * For instance, if you compare composite names like "TIAGO JOSE ADAMI" and
	 * "TIAGO ADAMI JOSE" the diff distance is 0.375.
	 * 
	 * With this method, the distance should be 1.0.
	 * 
	 * @param name1
	 * @param name2
	 * @return
	 */
	public static double nameSimilarity(String name1, String name2) {
		String find[] = { "Ã", "Á", "À", "Â", "É", "È", "Ê", "Í", "Î", "Ì", "Ò", "Ó", "Õ", "Ô", "Ù", "Ú", "Û", "Ç" };
		String swap[] = { "A", "A", "A", "A", "E", "E", "E", "I", "I", "I", "O", "O", "O", "O", "U", "U", "U", "C" };

		name1 = name1.trim().toUpperCase();
		name2 = name2.trim().toUpperCase();

		for (int i = 0; i < find.length; i++) {
			name1 = name1.replaceAll(find[i], swap[i]);
			name2 = name2.replaceAll(find[i], swap[i]);
		}

		String n1[] = name1.trim().split(" ");
		String n2[] = name2.trim().split(" ");

		BigDecimal d1 = new BigDecimal(StringUtils.calculateSimilarityArray(n1, n2));
		BigDecimal d2 = new BigDecimal(StringUtils.calculateSimilarityArray(n2, n1));

		double distance = (d1.add(d2)).divide(new BigDecimal(2)).doubleValue();

		return distance;
	}

	/**
	 * Returns the distance similarity for n1 using the highest similarity value
	 * for n1[i] within all elements of n2[].
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	private static double calculateSimilarityArray(String n1[], String n2[]) {
		double dis1[] = new double[n1.length];
		int pos = 0;

		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();

		for (String s : n1) {
			list1.add(s);
		}

		for (String s : n2) {
			list2.add(s);
		}

		/**
		 * Removes all perfect matches
		 */
		for (int i = 0; i < list1.size(); i++) {
			String s1 = list1.get(i);

			for (int j = 0; j < list2.size(); j++) {
				String s2 = list2.get(j);

				if (s1.trim().toUpperCase().equals(s2.trim().toUpperCase())) {
					list1.remove(i);
					list2.remove(j);
					dis1[pos] = 1.0d;
					pos++;
					i--;
					break;
				}
			}
		}

		/**
		 * 
		 */
		for (int i = 0; i < list1.size(); i++) {
			String s1 = list1.get(i);

			for (String s2 : list2) {
				double d = StringUtils.similarity(s1, s2);

				if (d > dis1[pos]) {
					dis1[pos] = d;
				}

				if (d == 1.0) {
					break;
				}
			}

			pos++;
		}

		BigDecimal distance = new BigDecimal(0);
		BigDecimal divisor = new BigDecimal(n1.length);

		for (int i = 0; i < dis1.length; i++) {
			BigDecimal d = new BigDecimal(dis1[i]);
			distance = distance.add(d);
		}

		distance = distance.divide(divisor, 6, RoundingMode.HALF_UP);

		return distance.doubleValue();
	}

}
