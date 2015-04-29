package com.adamiworks.mirrordb;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Properties;

import com.adamiworks.mirrordb.exception.PersistenceException;
import com.adamiworks.mirrordb.util.DatabaseParameter;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TransactionFactory {
	private Properties properties;

	public TransactionFactory(Properties properties) {
		super();
		this.properties = properties;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * This method creates a new cpds to the database using C3P0.
	 * 
	 * @return
	 * @throws PersistenceException
	 * @throws SQLException
	 */
	private ComboPooledDataSource createComboPooledDataSource() throws PersistenceException, SQLException {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			String driver = properties.getProperty(DatabaseParameter.DATABASE_DRIVER);
			String url = properties.getProperty(DatabaseParameter.DATABASE_URL);
			String user = properties.getProperty(DatabaseParameter.DATABASE_USER);
			String password = properties.getProperty(DatabaseParameter.DATABASE_PASSWORD);
			Integer increment = 0;
			Integer minPool = 0;
			Integer maxPool = 0;

			try {
				increment = Integer.valueOf(properties.getProperty(DatabaseParameter.DATABASE_POOL_INCREMENT));
			} catch (Exception e) {
				increment = 1;
			}

			try {
				minPool = Integer.valueOf(properties.getProperty(DatabaseParameter.DATABASE_POOL_MIN));
			} catch (Exception e) {
				minPool = 1;
			}

			try {
				maxPool = Integer.valueOf(properties.getProperty(DatabaseParameter.DATABASE_POOL_MAX));
			} catch (Exception e) {
				maxPool = 1;
			}

			cpds.setDriverClass(driver);
			cpds.setJdbcUrl(url);
			cpds.setUser(user);
			cpds.setPassword(password);
			cpds.setAcquireIncrement(increment);
			cpds.setMaxPoolSize(maxPool);
			cpds.setMinPoolSize(minPool);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new PersistenceException("An error ocurred trying to read configuration object");
		}

		return cpds;
	}
	
	public Transaction createTransaction() throws PersistenceException, SQLException{
		Transaction transaction = new Transaction(this.createComboPooledDataSource());
		return transaction;
	}

}
