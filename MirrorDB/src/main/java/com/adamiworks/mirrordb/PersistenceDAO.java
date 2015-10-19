package com.adamiworks.mirrordb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adamiworks.mirrordb.annotations.Column;
import com.adamiworks.mirrordb.annotations.Table;
import com.adamiworks.mirrordb.exception.PersistenceAnnotationNotFound;
import com.adamiworks.mirrordb.exception.PersistenceException;
import com.adamiworks.mirrordb.exception.PersistenceEntityWithoutPrimaryKey;
import com.adamiworks.mirrordb.util.ColumnDescriptor;
import com.adamiworks.mirrordb.util.NamedParameterStatement;
import com.adamiworks.mirrordb.util.ReflectionsUtil;

/**
 * Superclass for DAO implementations. Performs INSERT, DELETE and UPDATE based
 * on class definitions directly into database. This class does not uses any
 * cache of objects. It passes simple SQL Statements to JDBC driver, reducing
 * time consumption when developer wants no persistence solution like Hibernate.
 * 
 * DO NOT USE WITH HIBERNATE OR ANOTHER PERSISTENCE LAYER.
 * 
 * @author Tiago J. Adami
 *
 */
public abstract class PersistenceDAO<E> {

	private boolean debug = true;
	private Class<E> pojoClass;
	private List<ColumnDescriptor> primaryKeyColumns = null;
	private List<ColumnDescriptor> columns = null;
	private String sqlInsert = null;
	private String sqlUpdate = null;
	private String sqlDelete = null;
	private String sqlSelect = null;
	private String schemaName = null;
	private String fullTableName = null;
	private String tableName = null;
	private Transaction transaction = null;

	/**
	 * Creates a new Persistence Util triggering SQL Statements generation
	 * 
	 * @param transaction
	 * @throws PersistenceAnnotationNotFound
	 * @throws PersistenceEntityWithoutPrimaryKey
	 * @throws SQLException
	 */
	public PersistenceDAO(Transaction transaction)
			throws PersistenceAnnotationNotFound, PersistenceEntityWithoutPrimaryKey, SQLException {
		super();
		this.transaction = transaction;
		this.pojoClass = getTypeParameterClass();

		ReflectionsUtil ru = new ReflectionsUtil();
		this.primaryKeyColumns = ru.captureFields(pojoClass, true);
		this.columns = ru.captureFields(pojoClass, false);

		if (this.primaryKeyColumns.size() == 0) {
			throw new PersistenceEntityWithoutPrimaryKey(this.pojoClass);
		}

		this.sqlInsert = this.generateSqlInsert();
		this.sqlUpdate = this.generateSqlUpdate();
		this.sqlDelete = this.generateSqlDelete();
		this.sqlSelect = this.generateSqlSelect();

		retrieveTableName();
	}

	/**
	 * Set schema and table names.
	 * 
	 * @throws PersistenceAnnotationNotFound
	 */
	private void retrieveTableName() throws PersistenceAnnotationNotFound {
		String schema = null;
		String table = null;

		if (!pojoClass.isAnnotationPresent(Table.class)) {
			throw new PersistenceAnnotationNotFound(pojoClass, "Table");
		}

		schema = pojoClass.getAnnotation(Table.class).schema().trim().toUpperCase();
		table = pojoClass.getAnnotation(Table.class).name().trim().toUpperCase();

		this.schemaName = schema;
		this.tableName = table;
		this.fullTableName = ((schema != null && !schema.isEmpty()) ? schema + "." + table : table);
	}

	/**
	 * This inner method returns a class from the parametrized inferred data
	 * type.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Class<E> getTypeParameterClass() {
		Type type = getClass().getGenericSuperclass();
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<E>) paramType.getActualTypeArguments()[0];
	}

	/**
	 * Generates a SQL SELECT statement using all fields of POJO class
	 * 
	 * @return A String of SQL SELECT Statement
	 * @throws PersistenceAnnotationNotFound
	 */
	private String generateSqlSelect() throws PersistenceAnnotationNotFound {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(fullTableName).append(" ( ");

		// Get fields
		Field fields[] = pojoClass.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			if (!f.isAnnotationPresent(Column.class)) {
				throw new PersistenceAnnotationNotFound(pojoClass, "Column");
			}

			String columnName = f.getAnnotation(Column.class).name();

			sql.append(columnName);

			if (i + 1 == fields.length) {
				sql.append(" ");
			} else {
				sql.append(", ");
			}
		}

		sql.append("FROM ").append(fullTableName);

		return sql.toString();
	}

	/**
	 * Generates a String containing the SQL INSERT statement needed to insert
	 * rows into a database table
	 * 
	 * @return The string with SQL INSERT for a PreparedStatement
	 * @throws PersistenceAnnotationNotFound
	 *             raised when POJO does not have needed Annotations for class
	 *             and for attributes
	 */
	private String generateSqlInsert() throws PersistenceAnnotationNotFound {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		StringBuilder values = new StringBuilder("VALUES ( ");

		sql.append(fullTableName).append(" ( ");

		// Get fields
		Field fields[] = pojoClass.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			if (!f.isAnnotationPresent(Column.class)) {
				throw new PersistenceAnnotationNotFound(pojoClass, "Column");
			}

			String columnName = f.getAnnotation(Column.class).name();

			sql.append(columnName);
			values.append("?");

			if (i + 1 == fields.length) {
				sql.append(" ) ");
				values.append(" )");
			} else {
				sql.append(", ");
				values.append(", ");
			}
		}

		sql.append(values);

		return sql.toString();
	}

	/**
	 * Generates a String containing the SQL UPDATE statement needed to update
	 * rows of a database table
	 * 
	 * @return The string with SQL UPDATE for a PreparedStatement
	 * @throws PersistenceAnnotationNotFound
	 *             raised when POJO does not have needed Annotations for class
	 *             and for attributes
	 */
	private String generateSqlUpdate() throws PersistenceAnnotationNotFound {
		StringBuilder sql = new StringBuilder("UPDATE ");
		StringBuilder where = new StringBuilder("WHERE ");

		sql.append(fullTableName).append(" SET ");

		// Get fields
		Field fields[] = pojoClass.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			if (!f.isAnnotationPresent(Column.class)) {
				throw new PersistenceAnnotationNotFound(pojoClass, "Column");
			}

			String columnName = f.getAnnotation(Column.class).name();

			sql.append(columnName).append(" = ?");

			if (i + 1 == fields.length) {
				sql.append(" ");
			} else {
				sql.append(", ");
			}
		}

		for (int i = 0; i < primaryKeyColumns.size(); i++) {
			ColumnDescriptor c = primaryKeyColumns.get(i);
			String s = c.getAnnotatedName();
			where.append(s).append(" = ?");
			if (i + 1 < primaryKeyColumns.size()) {
				where.append(" AND ");
			}
		}

		sql.append(where);

		return sql.toString();
	}

	/**
	 * Generates a String containing the SQL DELETE statement needed to delete
	 * rows of a database table
	 * 
	 * @return The string with SQL DELETE for a PreparedStatement
	 * @throws PersistenceAnnotationNotFound
	 *             raised when POJO does not have needed Annotations for class
	 *             and for attributes
	 */
	private String generateSqlDelete() throws PersistenceAnnotationNotFound {
		StringBuilder sql = new StringBuilder("DELETE FROM ");
		StringBuilder where = new StringBuilder("WHERE ");

		sql.append(fullTableName).append(" ");

		for (int i = 0; i < primaryKeyColumns.size(); i++) {
			ColumnDescriptor c = primaryKeyColumns.get(i);
			String s = c.getAnnotatedName();
			where.append(s).append(" = ?");
			if (i + 1 < primaryKeyColumns.size()) {
				where.append(" AND ");
			}
		}

		sql.append(where);

		return sql.toString();
	}

	/**
	 * Performns a SQL INSERT using object's class definition and its values.
	 * 
	 * @param obj
	 *            Any POJO representing a database table with values.
	 * @throws PersistenceAnnotationNotFound
	 * @throws PersistenceException
	 */
	public void insert(E obj) throws PersistenceAnnotationNotFound, PersistenceException {
		PreparedStatement pstmt;

		try {
			if (debug) {
				System.out.println(sqlInsert);
			}

			pstmt = transaction.getConnection().prepareStatement(sqlInsert);
			for (int i = 0; i < this.columns.size(); i++) {
				ColumnDescriptor cd = this.columns.get(i);
				Object nulls[] = null;
				Object value = cd.getGetter().invoke(obj, nulls);

				if (value instanceof java.util.Date) {
					pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) value).getTime()));
				} else {
					pstmt.setObject(i + 1, value);
				}

			}

			pstmt.execute();
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage());
		}

	}

	/**
	 * Performns a SQL DELETE using object's class definition and its primary
	 * key values.
	 * 
	 * @param obj
	 *            Any POJO representing a database table with values.
	 * @throws PersistenceException
	 */
	public void delete(Object obj) throws PersistenceException {
		PreparedStatement pstmt;
		try {
			if (debug) {
				System.out.println(sqlDelete);
			}

			pstmt = transaction.getConnection().prepareStatement(sqlDelete);

			// primary key columns
			for (int i = 0; i < this.primaryKeyColumns.size(); i++) {
				ColumnDescriptor cd = this.primaryKeyColumns.get(i);
				Object nulls[] = null;
				Object value = cd.getGetter().invoke(obj, nulls);
				if (value instanceof java.util.Date) {
					pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) value).getTime()));
				} else {
					pstmt.setObject(i + 1, value);
				}
			}

			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage());
		}

	}

	/**
	 * Performns a SQL UPDATE using object's class definition and its values.
	 * 
	 * @param obj
	 *            Any POJO representing a database table with values.
	 * @throws PersistenceException
	 */
	public void update(E obj) throws PersistenceException {
		PreparedStatement pstmt;

		try {
			if (debug) {
				System.out.println(sqlUpdate);
			}

			pstmt = transaction.getConnection().prepareStatement(sqlUpdate);
			int x = 0;

			// First all data columns
			for (int i = 0; i < this.columns.size(); i++) {
				x++;
				ColumnDescriptor cd = this.columns.get(i);
				Object nulls[] = null;
				Object value = cd.getGetter().invoke(obj, nulls);

				if (value instanceof java.util.Date) {
					pstmt.setDate(x, new java.sql.Date(((java.util.Date) value).getTime()));
				} else {
					pstmt.setObject(x, value);
				}
			}

			// After all primary key columns
			for (int i = 0; i < this.primaryKeyColumns.size(); i++) {
				x++;
				ColumnDescriptor cd = this.primaryKeyColumns.get(i);
				Object nulls[] = null;
				Object value = cd.getGetter().invoke(obj, nulls);
				if (value instanceof java.util.Date) {
					pstmt.setDate(x, new java.sql.Date(((java.util.Date) value).getTime()));
				} else {
					pstmt.setObject(x, value);
				}
			}

			pstmt.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage());
		}

	}

	/**
	 * This is an auxiliary method for using SQL SELECT instructions and
	 * transfer all values from the current line of ResultSet to the object.
	 * 
	 * @param o
	 *            the Object
	 * @param rs
	 *            The ResultSet poiting to the row to be extracted
	 * @throws PersistenceException
	 * @throws SQLException
	 */
	private void retrieveValuesFromResultSet(Object o, ResultSet rs) throws PersistenceException, SQLException {
		if (rs.getRow() > 0) {
			try {
				// Rolling all columns
				for (ColumnDescriptor cd : columns) {
					Object value = rs.getObject(cd.getAnnotatedName());
					cd.getSetter().invoke(o, value);
				}
			} catch (Exception e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	/**
	 * Executes a SQL SELECT statement returning a ResultSet.
	 * 
	 * @param sql
	 * @param args
	 * @param limit
	 * @return
	 * @throws SQLException
	 */
	public ResultSet selectResultSet(String sql, Map<String, Object> args, int limit) throws SQLException {
		ResultSet rs = null;

		if (args == null || args.isEmpty()) {
			Statement stmt = transaction.getConnection().createStatement();
			if (limit > 0) {
				stmt.setMaxRows(limit);
			}
			rs = stmt.executeQuery(sql);

		} else {
			NamedParameterStatement nstmt = new NamedParameterStatement(transaction.getConnection(), sql);
			Set<String> keys = args.keySet();
			for (String s : keys) {
				Object value = args.get(s);
				if (value instanceof java.util.Date) {
					nstmt.setDate(s, (Date) value);
				} else {
					nstmt.setObject(s, value);
				}
			}
			rs = nstmt.executeQuery();
		}
		return rs;
	}

	/**
	 * Executes a SQL SELECT and returns a list of objects found.
	 * 
	 * @param sql
	 *            The SQL SELECT clause without
	 * @param args
	 *            A Map for each parameter name in {0} and its value in
	 *            Object{1}.
	 * @param limit
	 *            Max number of objects to return
	 * @return A list of objects extracted from ResultSet
	 * @throws PersistenceException
	 *             if any internal exception is thrown
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<E> selectObjects(String sql, Map<String, Object> args, int limit)
			throws PersistenceException, SQLException {
		List<E> list = null;

		try {
			list = new ArrayList<E>();

			ResultSet rs = this.selectResultSet(sql, args, limit);

			while (rs.next()) {
				Class<?> clazz = Class.forName(pojoClass.getName());
				Constructor<?> ctor = clazz.getConstructor();
				Object object = ctor.newInstance(new Object[] {});
				this.retrieveValuesFromResultSet(object, rs);
				list.add(((E) object));
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new PersistenceException("Class not found [" + pojoClass.getName() + "]");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new PersistenceException(
					"No default Constructor method (without arguments) for [" + pojoClass.getName() + "]");
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new PersistenceException("SecurityException trying to instantiate [" + pojoClass.getName() + "]");
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new PersistenceException("Could not instantiate [" + pojoClass.getName() + "]");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new PersistenceException(
					"IllegalAccessException trying to instantiate [" + pojoClass.getName() + "]");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new PersistenceException(
					"IllegalArgumentException trying to instantiate [" + pojoClass.getName() + "]");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new PersistenceException(
					"InvocationTargetException trying to instantiate [" + pojoClass.getName() + "]");
		}

		return list;
	}

	/**
	 * Executes a SQL SELECT and returns a list of objects found without limit
	 * of rows.
	 * 
	 * @param sql
	 *            The SQL SELECT clause without
	 * @param args
	 *            A Map for each parameter name in {0} and its value in
	 *            Object{1}.
	 * @return A list of objects extracted from ResultSet
	 * @throws PersistenceException
	 *             if any internal exception is thrown
	 * @throws SQLException
	 */
	public List<E> selectObjects(String sql, Map<String, Object> args) throws PersistenceException, SQLException {
		return this.selectObjects(sql, args, 0);
	}

	/**
	 * Performs a SQL SELECT into the database and return the unique object
	 * found. If more than object is found, an PersistenceException is raised.
	 * 
	 * @param sql
	 * @param args
	 * @return
	 * @throws PersistenceException
	 * @throws SQLException
	 */
	public E selectOneObject(String sql, Map<String, Object> args) throws PersistenceException, SQLException {
		List<E> list = this.selectObjects(sql, args);

		if (list.size() > 1) {
			throw new PersistenceException(
					"More than one object found. Review your SQL statement [" + pojoClass.getName() + "]");
		}

		return list.get(0);
	}

	/**
	 * <B>CAUTION!!!</B>
	 * 
	 * This method forces a SQL UPDATE on primary key columns on rows when WHERE
	 * condition applies forcing a ROW LOCK EXCLUSIVE on them. After the
	 * transaction ends, the rows are also UPDATE generating a fragmentation in
	 * the table even if data was not changed.
	 * 
	 * Be warned about fragmentation implications that this method results.
	 * 
	 * @param where
	 *            The condition to force rows to be locked
	 * @throws PersistenceException
	 */
	public void lockRowsWithUpdate(String where) throws PersistenceException {
		try {
			StringBuilder sql = new StringBuilder("UPDATE ");
			sql.append(this.fullTableName).append(" ");
			sql.append("SET ");

			for (int i = 0; i < this.primaryKeyColumns.size(); i++) {
				ColumnDescriptor cd = this.primaryKeyColumns.get(i);
				sql.append(cd.getAnnotatedName()).append("=").append(cd.getAnnotatedName());
				sql.append(i + 1 == this.primaryKeyColumns.size() ? " " : ", ");
			}

			sql.append("WHERE ");
			sql.append(where);

			System.out.println("\n\n###");
			System.out.println("LOCK BEING APPLIED:");
			System.out.println(sql.toString());
			System.out.println("\n\n###");

			Statement s = transaction.getConnection().createStatement();
			s.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage());
		}
	}

	/**
	 * Retrieves a map containing the PK values of the object.
	 * 
	 * @param obj
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Map<String, Object> getArgsFromPk(E obj)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> map = new HashMap<String, Object>();

		for (int i = 0; i < this.primaryKeyColumns.size(); i++) {
			ColumnDescriptor cd = this.primaryKeyColumns.get(i);
			Object nulls[] = null;
			Object value = cd.getGetter().invoke(obj, nulls);
			map.put(cd.getAnnotatedName(), value);
		}

		return map;
	}

	public Connection getConnection() throws SQLException {
		return transaction.getConnection();
	}

	public Class<E> getPojoClass() {
		return pojoClass;
	}

	public List<ColumnDescriptor> getPrimaryKeyColumns() {
		return primaryKeyColumns;
	}

	public List<ColumnDescriptor> getColumns() {
		return columns;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getSqlSelect() {
		return sqlSelect;
	}

	public String getTableName() {
		return tableName;
	}

	public String getSqlInsert() {
		return sqlInsert;
	}

	public String getSqlUpdate() {
		return sqlUpdate;
	}

	public String getSqlDelete() {
		return sqlDelete;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getFullTableName() {
		return fullTableName;
	}

}
