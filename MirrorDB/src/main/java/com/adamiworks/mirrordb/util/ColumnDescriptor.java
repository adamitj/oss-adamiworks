package com.adamiworks.mirrordb.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Class to represent a database column with ability to use Java Reflections.
 * 
 * @author Tiago J. Adami
 *
 */
public class ColumnDescriptor {
	private Field field;
	private String annotatedName;
	private Method getter;
	private Method setter;

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getAnnotatedName() {
		return annotatedName;
	}

	public void setAnnotatedName(String annotatedName) {
		this.annotatedName = annotatedName;
	}

	public Method getGetter() {
		return getter;
	}

	public void setGetter(Method getter) {
		this.getter = getter;
	}

	public Method getSetter() {
		return setter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

}
