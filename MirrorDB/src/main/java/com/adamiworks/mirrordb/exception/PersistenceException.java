package com.adamiworks.mirrordb.exception;

public class PersistenceException extends Exception {

	private static final long serialVersionUID = 1152744877889752117L;

	public PersistenceException(String reason){
		super("Persistence Error: "+reason);
	}
	
}
