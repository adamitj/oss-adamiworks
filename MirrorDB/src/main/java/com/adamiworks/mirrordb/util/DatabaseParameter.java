package com.adamiworks.mirrordb.util;

/**
 * Contains parameter names
 * 
 * @author Tiago
 *
 */
public final class DatabaseParameter {
	private DatabaseParameter() {
		//
	}

	public static final String DATABASE_DRIVER = "database.driver";
	public static final String DATABASE_URL = "database.url";
	public static final String DATABASE_USER = "database.user";
	public static final String DATABASE_PASSWORD = "database.password";
	public static final String DATABASE_POOL_MIN = "database.pool.min";
	public static final String DATABASE_POOL_MAX = "database.pool.max";
	public static final String DATABASE_POOL_INCREMENT = "database.pool.increment";

}