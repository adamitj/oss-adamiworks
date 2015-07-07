package com.adamiworks.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to generate a Map from an object List indexed by an object
 * attribute
 * 
 * @author Tiago J. Adami
 *
 * @param <T>
 *            The Class of the indexed object
 */
public abstract class MapList<T> {

	private List<T> list = null;

	/**
	 * You should pass the list containing the object to this list
	 * 
	 * @param list
	 */
	public MapList(List<T> list) {
		super();
		this.list = list;
	}

	/**
	 * This method get the index attribute value for each row in List
	 * 
	 * @param t
	 * @return
	 */
	protected abstract Object getIndexValue(T t);

	/**
	 * Retrieves a map reflecting the given list indexed by the class attribute
	 * 
	 * @return
	 */
	public Map<Object, T> getMap() {
		Map<Object, T> map = new HashMap<Object, T>();

		for (T t : list) {
			map.put(getIndexValue(t), t);
		}

		return map;
	}

}
