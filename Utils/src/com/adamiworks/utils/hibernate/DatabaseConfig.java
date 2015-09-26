package com.adamiworks.utils.hibernate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.adamiworks.utils.FileUtils;

/**
 * This class always search for a <b>config.properties</b> file in the classpath
 * of the application
 * 
 * @author adami
 *
 */
public class DatabaseConfig {
	private static DatabaseConfig self;

	private Properties properties;

	/**
	 * Private constructor
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private DatabaseConfig() {
		try {
			properties = FileUtils.getPropertiesFromClasspath("config.properties");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Singleton method
	 * 
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static DatabaseConfig getInstance() {
		if (self == null) {
			self = new DatabaseConfig();
		}
		return self;
	}

	/**
	 * Get the Properties object
	 * 
	 * @return
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Returns the given parameter from properties file
	 * 
	 * @param p
	 * @return
	 */
	public String getProperty(DatabaseParameter p) {
		return this.properties.getProperty(p.getParameter());
	}

}
