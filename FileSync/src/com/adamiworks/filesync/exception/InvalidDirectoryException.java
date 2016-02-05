package com.adamiworks.filesync.exception;

public class InvalidDirectoryException extends Exception {

	private static final long serialVersionUID = -5347631222605848367L;

	public InvalidDirectoryException(String s) {
		super();
		System.out.println("Directory " + s + " is invalid.");
	}

}
