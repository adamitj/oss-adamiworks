package com.adamiworks.mirrordb.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.adamiworks.mirrordb.annotations.Column;
import com.adamiworks.mirrordb.exception.PersistenceAnnotationNotFound;

/**
 * Utility to deal with Reflections within Claws Library
 * 
 * @author Tiago
 *
 */
public class ReflectionsUtil {

	/**
	 * Converts the first character of a String into upper case
	 * 
	 * @param text
	 * @return
	 */
	private String capitalize(String text) {
		text = text.substring(0, 1).toUpperCase() + text.substring(1);
		return text;
	}

	/**
	 * Capture all class' attributes into a list of ColumnDescriptor
	 * 
	 * @param pojoClass
	 *            SubClass of PersistentEntity
	 * @param onlyPrimaryKeys
	 *            true if only primary key columns
	 * @return
	 * @throws PersistenceAnnotationNotFound
	 */
	public List<ColumnDescriptor> captureFields(Class<?> pojoClass, boolean onlyPrimaryKeys) throws PersistenceAnnotationNotFound {
		List<ColumnDescriptor> list = new ArrayList<ColumnDescriptor>();
		list = fieldToColumnDescriptor(pojoClass, onlyPrimaryKeys);
		return list;
	}

	/**
	 * Extract all fields and returns a list of ColumnDescriptor
	 * 
	 * @param pojoClass
	 *            SubClass of PersistentEntity
	 * @param onlyPks
	 * @return
	 * @throws PersistenceAnnotationNotFound
	 */
	private List<ColumnDescriptor> fieldToColumnDescriptor(Class<?> pojoClass, boolean onlyPks) throws PersistenceAnnotationNotFound {

		Field fields[] = pojoClass.getDeclaredFields();
		List<ColumnDescriptor> list = new ArrayList<ColumnDescriptor>();

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			if (!f.isAnnotationPresent(Column.class)) {
				throw new PersistenceAnnotationNotFound(pojoClass, "Column");
			}

			if (!onlyPks || f.getAnnotation(Column.class).primaryKey() == onlyPks) {

				String columnName = f.getAnnotation(Column.class).name();

				ColumnDescriptor c = new ColumnDescriptor();
				c.setField(f);
				c.setAnnotatedName(columnName);

				String getterMethodName = "get" + capitalize(f.getName());
				String setterMethodName = "set" + capitalize(f.getName());
				Method[] methods = pojoClass.getMethods();

				for (Method m : methods) {
					if (m.getName().equals(getterMethodName)) {
						c.setGetter(m);
						break;
					}
				}

				for (Method m : methods) {
					if (m.getName().equals(setterMethodName)) {
						c.setSetter(m);
						break;
					}
				}

				list.add(c);
			}
		}
		return list;
	}
}
