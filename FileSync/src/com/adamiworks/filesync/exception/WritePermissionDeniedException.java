package com.adamiworks.filesync.exception;

public class WritePermissionDeniedException extends Exception {

	private static final long serialVersionUID = -5347631222605848367L;

	public WritePermissionDeniedException(String s) {
		super();
		System.out.println("Cannot write into " + s + ". Permission denied.");
	}

}
