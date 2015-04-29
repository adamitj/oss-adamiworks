package com.adamiworks.utils.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.tomcat.util.codec.binary.Base64;

public class CryptoUtils {

	private byte[] keyBytes;
	private String internalMode;
	private CryptoMode mode;

	/**
	 * Creates a new CryptoUtil object to encode and decode Strings with AES or
	 * DES converting them into/from a Base64 readable String.
	 * 
	 * Notice that AES uses 128-bit keys and DES uses 56-bit keys (+1 bit for
	 * parity each byte).
	 * 
	 * That means you need to use a <b>16 length key for AES</b> and <b>8 length
	 * key for DES.</b>
	 * 
	 * @param mode
	 * @param symetricKey
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	public CryptoUtils(CryptoMode mode, String symetricKey) throws InvalidKeyException, NoSuchAlgorithmException {
		this.mode = mode;
		keyBytes = symetricKey.getBytes();

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
	 * Creates a new CryptoUtil object to encode and decode Strings with AES or
	 * DES converting them into/from a Base64 readable String.
	 * 
	 * Notice that AES uses 128-bit keys and DES uses 56-bit keys (+1 bit for
	 * parity each byte).
	 * 
	 * That means you need to use a <b>16 bytes for AES key</b> and <b>8 bytes
	 * for DES key</b>.
	 * 
	 * @param mode
	 * @param symetricKey
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	public CryptoUtils(CryptoMode mode, byte[] keyBytes) throws InvalidKeyException, NoSuchAlgorithmException {
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
	 * Returns the Cipher
	 * 
	 * @param cipherMode
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	private Cipher getCipher(byte[] iv, int cipherMode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
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
	public String encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
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
	public String decrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
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
