package com.adamiworks.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {
	/**
	 * Turn the first letter of a String to uppercase
	 * 
	 * @param s
	 * @return
	 */
	public static String capitalize(String s) {
		StringBuilder sb = new StringBuilder(StringUtils.right(s,
				s.length() - 1));
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

}
