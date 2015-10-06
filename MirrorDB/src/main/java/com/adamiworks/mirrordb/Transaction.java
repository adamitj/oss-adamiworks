package com.adamiworks.mirrordb;

import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Represents a database Transaction Session.
 * 
 * @author Tiago
 *
 */
public class Transaction {

	private Connection connection;
	private ComboPooledDataSource cpds;
	private boolean autoCommit = false;

	Transaction(ComboPooledDataSource cpds) {
		this.cpds = cpds;
	}

	/**
	 * Inicia uma nova transação em uma nova conexão.
	 * 
	 * @throws SQLException
	 */
	public void begin() throws SQLException {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (Exception e) {
				//
			} finally {
				try {
					connection.close();
				} catch (Exception e) {
					//
				}
			}
		}
		
		connection = cpds.getConnection();
		connection.setAutoCommit(autoCommit);
	}

	/**
	 * Encerra uma transação, desconectando-a do banco de dados;
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		connection.close();
		connection = null;
	}

	/**
	 * Cancela uma UOW completamente.
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		connection.rollback();
	}

	/**
	 * Persiste as informações no banco de dados permanentemente.
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		connection.commit();
	}

	/**
	 * Retorna o ComboPooledDataSource
	 * 
	 * @return
	 */
	public ComboPooledDataSource getComboPooledDataSource() {
		return cpds;
	}

	public Connection getConnection() throws SQLException {
		return connection;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.autoCommit = autoCommit;
		if (connection != null) {
			connection.setAutoCommit(true);
		}
	}

}
