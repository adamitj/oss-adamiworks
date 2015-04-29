package com.adamiworks.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

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
}
