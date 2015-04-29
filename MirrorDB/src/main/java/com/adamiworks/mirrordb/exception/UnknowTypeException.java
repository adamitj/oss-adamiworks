package com.adamiworks.mirrordb.exception;

public class UnknowTypeException extends Exception {

	private static final long serialVersionUID = -8208671981176273728L;

	public UnknowTypeException(String type) {
		super("Type " + type + " does not have a Java equivalent mapping");
	}

}
