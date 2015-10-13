package com.adamiworks.mirrordb.exception;

public class PersistenceEntityWithoutPrimaryKey extends Exception {

	private static final long serialVersionUID = -769242542627013427L;

	public PersistenceEntityWithoutPrimaryKey(Class<?> clazz) {
		super("No primary key is defined for " + clazz.getCanonicalName());
	}

}
