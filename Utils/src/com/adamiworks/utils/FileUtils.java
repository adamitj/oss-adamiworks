package com.adamiworks.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

	/**
	 * Read and loads a Properties file anywhere in classpath
	 * 
	 * @param propFileName
	 * @return
	 * @throws IOException
	 */
	public static Properties getPropertiesFromClasspath(String propFileName) throws IOException {
		Properties props = new Properties();
		InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(propFileName);

		if (inputStream == null) {
			throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
		}

		props.load(inputStream);
		return props;
	}

	/**
	 * Read an entire text file available in classpath.
	 * 
	 * @param fullPathAndfileName
	 * @return
	 */
	public static String readTextFile(File file) {
		return readTextFile(file.getPath());
	}
	
	/**
	 * Read an entire text file available in classpath.
	 * 
	 * @param fullPathAndfileName
	 * @return
	 */
	public static String readTextFile(String fullPathAndfileName) {
		InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(fullPathAndfileName);
		Scanner scanner = new Scanner(is);
		StringBuilder sb = new StringBuilder();

		while (scanner.hasNextLine()) {
			sb.append(scanner.nextLine()).append("\n");
		}

		scanner.close();

		return sb.toString();
	}

	/**
	 * Appends a new line to a text file. If file doesn't exists it is created.
	 * 
	 * @param file
	 * @param line
	 * @throws IOException
	 */
	public static void appendLineToTextFile(File file, String line) throws IOException {
		boolean newLine = true;

		if (!file.exists()) {
			file.createNewFile();
			newLine = false;
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		if (newLine) {
			bw.newLine();
		}
		bw.write(line);
		bw.close();
	}

	/**
	 * Generates MD5 hash for a given file.
	 * 
	 * @param file
	 * @return
	 */
	public static String getHashMD5(File file) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);

			byte[] dataBytes = new byte[1024];

			int nread = 0;

			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}

			fis.close();

			byte[] mdbytes = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Use one of 3 methods to copy a file from source to destination. Three
	 * methods are chained, if one fails the next assumes execution. The methods
	 * are ordered beginning with the most secure.
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public static void fileCopy(File source, File dest) {
		try {
			copyFileUsingJava7Files(source, dest);
		} catch (IOException e) {
			e.printStackTrace();

			System.err.println();
			Logger.getLogger(FileUtils.class.getName()).log(Level.WARNING,
					"Error copying file " + source.getName() + ". Using Channel method.");
			Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, e.getMessage());
			try {
				copyFileUsingChannel(source, dest);
			} catch (IOException e1) {
				e1.printStackTrace();
				Logger.getLogger(FileUtils.class.getName()).log(Level.WARNING,
						"Error copying file " + source.getName() + ". Using Byte method.");
				Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, e1.getMessage());

				try {
					copyFileUsingStream(source, dest);
				} catch (IOException e2) {
					e2.printStackTrace();
					Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE,
							"Error copying file " + source.getName() + ".");
					Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, e2.getMessage());
				}
			}
		}
	}

	/**
	 * Method used to copy files using Java Stream buffers.
	 * 
	 * All credits to Pankaj
	 * http://www.journaldev.com/861/4-ways-to-copy-file-in-java
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	private static void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	/**
	 * Method used to copy files using Java FileChannel.
	 * 
	 * All credits to Pankaj
	 * http://www.journaldev.com/861/4-ways-to-copy-file-in-java
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private static void copyFileUsingChannel(File source, File dest) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destChannel = new FileOutputStream(dest).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			sourceChannel.close();
			destChannel.close();
		}
	}

	/**
	 * Method used to copy files using Java 7 Files class.
	 * 
	 * All credits to Pankaj
	 * http://www.journaldev.com/861/4-ways-to-copy-file-in-java
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	private static void copyFileUsingJava7Files(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath());
	}

	/**
	 * Returns a list of directory content
	 * 
	 * @param sourceParentFolder
	 * @return
	 */
	public static String[] getDirList(File dir, boolean fullPath) {
		List<String> list = new ArrayList<String>();

		File listOfFiles[] = dir.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (fullPath) {
				list.add(listOfFiles[i].getPath());
			} else {
				list.add(listOfFiles[i].getName());
			}
		}

		String ret[] = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			ret[i] = s;
		}

		return ret;
	}

}
