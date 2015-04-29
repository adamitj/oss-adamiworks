package com.adamiworks.mirrordb.exception;

public class PersistenceAnnotationNotFound extends Exception {

	private static final long serialVersionUID = -756840468763280053L;

	public PersistenceAnnotationNotFound(Class<?> clazz, String annotation) {
		super("Annotation \"" + annotation + "\" is not present in class "
				+ clazz.getCanonicalName());
	}

}
