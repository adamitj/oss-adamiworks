package com.adamiworks.utils.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class that abstract common DES and AES encription functions.
 * 
 * @author adami
 *
 */
public class CryptoUtils {

	private byte[] keyBytes;
	private String internalMode;
	private CryptoMode mode;

	/**
	 * Creates a new CryptoUtil object to encode and decode Strings with AES or DES
	 * converting them into/from a Base64 readable String.
	 * 
	 * This constructor initialize the object using a given password encryption
	 * String of your choice.
	 * 
	 * @param mode
	 * @param passwordKey
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public CryptoUtils(CryptoMode mode, String passwordKey)
			throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		byte keyBytes[] = null;

		switch (mode) {
			case AES:
				keyBytes = getHash16String(passwordKey).getBytes();
				break;
			case DES:
				keyBytes = getHash8String(passwordKey).getBytes();
				break;
			default:
				break;
		}

		this.initialize(mode, keyBytes);
	}

	/**
	 * Creates a new CryptoUtil object to encode and decode Strings with AES or DES
	 * converting them into/from a Base64 readable String.
	 * 
	 * Notice that AES uses 128-bit keys and DES uses 56-bit keys (+1 bit for parity
	 * each byte).
	 * 
	 * That means you need to use a <b>16 bytes for AES key</b> and <b>8 bytes for
	 * DES key</b>.
	 * 
	 * @param mode
	 * @param symetricKey
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	public CryptoUtils(CryptoMode mode, byte[] keyBytes) throws InvalidKeyException, NoSuchAlgorithmException {
		initialize(mode, keyBytes);
	}

	/**
	 * Initialize the cripto mode and key
	 * 
	 * @param mode
	 * @param keyBytes
	 */
	private void initialize(CryptoMode mode, byte[] keyBytes) {
		this.mode = mode;
		this.keyBytes = keyBytes;

		switch (mode) {
			case AES:
				internalMode = "AES/CBC/PKCS5Padding";
				break;

			case DES:
				internalMode = "DES/CBC/PKCS5Padding";
				break;

			default:
				break;
		}
	}

	/**
	 * Gets a MD5 hash value for a given text.
	 * 
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	private static String getMD5(String arg) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
	}

	/**
	 * Generates a 16 char string hash from a given String text. <b>This is a custom
	 * application implementation. Not following any standard.</b>
	 * 
	 * @param text The plain string to get the 16 hash value based on MD5.
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static String getHash16String(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String md5hash = getMD5(text);

		// Get the first char from the md5hash
		String hash16 = md5hash.substring(0, 1);

		// Will follow to get remaining 15 chars only
		for (int i = 0; i < 15; i++) {
			// Hash from the composite existing hash16 + given String + the current
			// iteration, but gets only the first char to add.
			hash16 += (getMD5(hash16 + md5hash + String.valueOf(i+1))).substring(0, 1);
		}

		return hash16;
	}

	/**
	 * Generates a 8 char string hash from a given String text. <b>This is a custom
	 * application implementation. Not following any standard.</b>
	 * 
	 * @param text The plain string to get the 16 hash value based on MD5.
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public static String getHash8String(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String md5hash = getMD5(text);

		// Get the second char from the md5hash
		String hash8 = md5hash.substring(1, 2);

		// Will follow to get remaining 8 chars only
		for (int i = 0; i < 7; i++) {
			// Hash from the composite existing hash8 + given String + the current
			// iteration
			hash8 += (getMD5(hash8 + md5hash + String.valueOf(i+2))).substring(0,1);
		}

		return hash8;
	}

	/**
	 * Returns the Cipher
	 * 
	 * @param cipherMode
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	private Cipher getCipher(byte[] iv, int cipherMode) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(internalMode);
		SecretKeySpec keySpec = null;

		IvParameterSpec ivspec = new IvParameterSpec(iv);

		switch (mode) {
			case AES:
				keySpec = new SecretKeySpec(keyBytes, "AES");
				break;
			case DES:
				keySpec = new SecretKeySpec(keyBytes, "DES");
				break;
			default:
				break;
		}

		cipher = Cipher.getInstance(internalMode);
		cipher.init(cipherMode, keySpec, ivspec);

		return cipher;
	}

	/**
	 * Encript a String text using object Key and method.
	 * 
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String encrypt(String text)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
		byte[] input = text.getBytes("UTF-8");
		byte[] output = null;
		int offset = 0;

		byte[] iv = null;
		switch (mode) {
			case AES:
				offset = 16;
				break;
			case DES:
				offset = 8;
				break;
			default:
				break;
		}

		iv = new byte[offset];

		(new Random()).nextBytes(iv);

		Cipher cipher = getCipher(iv, Cipher.ENCRYPT_MODE);
		output = cipher.doFinal(input);

		int total = output.length + offset;

		byte[] outputWithIV = new byte[total];

		for (int i = 0; i < total; i++) {
			if (i < offset) {
				outputWithIV[i] = iv[i];
			} else {
				outputWithIV[i] = output[i - offset];
			}
		}

		String ret = Base64.getEncoder().encodeToString(outputWithIV);
		return ret;
	}

	/**
	 * Decript a previously encripted String text using object key and method.
	 * 
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String decrypt(String text)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
		byte[] output = null;
		byte[] input = Base64.getDecoder().decode(text);

		int offset = 0;

		byte[] iv = null;
		switch (mode) {
			case AES:
				offset = 16;
				break;
			case DES:
				offset = 8;
				break;
			default:
				break;
		}

		iv = new byte[offset];

		byte[] encrypted = new byte[input.length - offset];

		for (int i = 0; i < input.length; i++) {
			if (i < offset) {
				iv[i] = input[i];
			} else {
				encrypted[i - offset] = input[i];
			}
		}

		Cipher cipher = getCipher(iv, Cipher.DECRYPT_MODE);
		output = cipher.doFinal(encrypted);

		return new String(output);
	}
}
