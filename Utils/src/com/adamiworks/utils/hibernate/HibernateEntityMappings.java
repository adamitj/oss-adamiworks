package com.adamiworks.utils.hibernate;

import java.util.List;

import org.hibernate.cfg.Configuration;

import com.adamiworks.utils.ClassUtils;

public class HibernateEntityMappings {

	/**
	 * Used by DatabaseConfig to add classes to Hibernate Persistence Manager.
	 * 
	 * @param config
	 *            The Hibernate Configuration instance
	 * @param fullPackageName
	 *            The complete name of the package containing the entity
	 *            classes. Example: <b>com.adamiworks.appname.model</b>
	 */
	public static void loadClasses(Configuration config, String fullPackageName) {
		List<Class<?>> classes;
		try {
			classes = ClassUtils.getClassesForPackage(fullPackageName);
			for (Class<?> c : classes) {
				config.addAnnotatedClass(c);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
