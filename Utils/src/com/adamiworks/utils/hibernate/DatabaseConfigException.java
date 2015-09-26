package com.adamiworks.utils.hibernate;

public class DatabaseConfigException extends Exception {

	private static final long serialVersionUID = -5558016537855154617L;

	public DatabaseConfigException() {
		super("Invalid parameter at config.properties");
	}

}
