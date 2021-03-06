package com.adamiworks.mirrordb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import com.adamiworks.mirrordb.exception.PersistenceException;
import com.adamiworks.mirrordb.exception.UnknowTypeException;

/**
 * This piece of code uses the information passed as arguments to executable
 * class named MainApp and create the entities for each table.
 * 
 * @author Tiago
 *
 */
public class EntityGenerator {

	private String propertiesFileName;
	private String packageName;
	private String outputPathName;
	private String schemaName;
	private SimpleDateFormat sdfTimestamp;

	/**
	 * Prepares the object to process all the tables of the database.
	 * 
	 * @param propertiesFileName
	 * @param packageName
	 * @param outputPathName
	 * @param schemaName
	 */
	public EntityGenerator(String propertiesFileName, String packageName, String outputPathName, String schemaName) {
		super();
		this.propertiesFileName = propertiesFileName;
		this.packageName = packageName;
		this.outputPathName = outputPathName;
		this.schemaName = schemaName.trim().toLowerCase();

		sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * Generate an entity class for each database table of all schemas.
	 */
	public void generateEntities() {
		try {
			// Get properties file
			File propertiesFile = new File(propertiesFileName);
			InputStream inputStream = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(inputStream);

			// 1-Connect to DB
			TransactionFactory factory = new TransactionFactory(properties);
			Transaction trans = factory.createTransaction();
			trans.begin();

			// 2-Read Metadata
			String tableTypes[] = { "TABLE", "VIEW", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM" };
			DatabaseMetaData dmd = trans.getConnection().getMetaData();
			ResultSet rsTables = dmd.getTables(null, schemaName, null, tableTypes);

			// 3-Create pojo files
			StringBuilder sb = null;
			while (rsTables.next()) {
				Calendar timestamp = Calendar.getInstance();
				sb = new StringBuilder();
				String tableSchema = rsTables.getString("TABLE_SCHEM");
				String tableName = rsTables.getString("TABLE_NAME");
				String tableRemarks = rsTables.getString("REMARKS");
				String className = this.toCamelCaseClass(tableName);

				System.out.print("Generating class " + className);

				sb.append("/*\n");
				sb.append(" * THIS CLASS WAS GENERATED BY ").append(MainApp.APP_NAME).append(" v.").append(MainApp.APP_VERSION).append("\n");
				sb.append(" * Creation time: ").append(sdfTimestamp.format(timestamp.getTime())).append("\n");
				sb.append(" */\n\n");
				sb.append("package ").append(packageName).append(";\n\n");
				sb.append("import com.adamiworks.mirrordb.annotations.Column;\n");
				sb.append("import com.adamiworks.mirrordb.annotations.Table;\n\n");

				if (tableRemarks != null && tableRemarks.trim().length() > 0) {
					sb.append("/**\n");
					sb.append(" * ").append(tableRemarks).append("\n");
					sb.append(" */\n");
				}

				sb.append("@Table(name=\"").append(tableName).append("\", schema=\"").append(schemaName).append("\")\n");
				sb.append("public class ").append(className).append(" {\n\n");

				System.out.print(".");

				// Get table primary keys
				ResultSet rsPk = dmd.getPrimaryKeys(null, tableSchema, tableName);
				List<EntityGenerator.Attribute> listAttributes = new ArrayList<EntityGenerator.Attribute>();
				while (rsPk.next()) {
					String name = rsPk.getString("COLUMN_NAME");
					Attribute att = new Attribute(name, true, null, null);
					listAttributes.add(att);
				}

				System.out.print(".");

				// Roll among all columns
				ResultSet rsCol = dmd.getColumns(null, tableSchema, tableName, null);
				while (rsCol.next()) {
					String name = rsCol.getString("COLUMN_NAME");
					String remarks = rsCol.getString("REMARKS");
					String type = rsCol.getString("TYPE_NAME");
					boolean isPk = false;

					for (Attribute att : listAttributes) {
						if (att.name.equals(name)) {
							att.remarks = remarks;
							att.type = type;
							isPk = true;
							break;
						}
					}

					if (!isPk) {
						Attribute att = new Attribute(name, false, remarks, type);
						listAttributes.add(att);
					}
				}

				System.out.print(".");

				// All columns to Attributes
				for (Attribute a : listAttributes) {
					String type = EntityTypeMapping.getType(a.type);

					if (a.remarks != null && a.remarks.trim().length() > 0) {
						sb.append("\t/**\n");
						sb.append("\t * ").append(a.remarks.trim()).append("\n");
						sb.append("\t */\n");
					}

					sb.append("\t@Column(name=\"").append(a.name).append("\", primaryKey=").append(a.pk ? "true" : "false").append(")\n");
					sb.append("\tprivate ").append(type).append(" ").append(this.toCamelCaseAttribute(a.name)).append(";\n\n");
				}

				System.out.print(".");

				// All columns public getters and setters
				for (Attribute a : listAttributes) {
					String type = EntityTypeMapping.getType(a.type);
					String setterName = "set" + this.toCamelCaseClass(a.name);
					String getterName = "get" + this.toCamelCaseClass(a.name);
					String variableName = this.toCamelCaseAttribute(a.name);

					// getter
					if (a.remarks != null && a.remarks.trim().length() > 0) {
						sb.append("\t/**\n");
						sb.append("\t * ").append(a.remarks.trim()).append("\n");
						sb.append("\t */\n");
					}
					sb.append("\tpublic ").append(type).append(" ").append(getterName).append("() {\n");
					sb.append("\t\treturn this.").append(variableName).append(";\n");
					sb.append("\t}\n\n");

					// setter
					if (a.remarks != null && a.remarks.trim().length() > 0) {
						sb.append("\t/**\n");
						sb.append("\t * ").append(a.remarks.trim()).append("\n");
						sb.append("\t */\n");
					}
					sb.append("\tpublic void ").append(setterName).append("(").append(type).append(" ").append(variableName).append(") {\n");
					sb.append("\t\tthis.").append(variableName).append(" = ").append(variableName).append(";\n");
					sb.append("\t}\n\n");
				}

				sb.append("}");

				this.writeFile(className, sb);

				System.out.println(" done!");
				// System.out.println(sb.toString());
				// System.out.println("=======================");
			}

			trans.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (PersistenceException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnknowTypeException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Transforms a String with underscores "_" into a camel case String
	 * intended to be a Class name
	 * 
	 * @param input
	 * @return
	 */
	public String toCamelCaseClass(String input) {
		input = input.toUpperCase().trim();
		StringBuilder sb = new StringBuilder();
		for (String oneString : input.split("_")) {
			sb.append(oneString.substring(0, 1));
			sb.append(oneString.substring(1).toLowerCase());
		}
		return sb.toString();
	}

	/**
	 * Transforms a String with underscores "_" into a camel case String
	 * intended to be an Attribute name.
	 * 
	 * @param input
	 * @return
	 */
	public String toCamelCaseAttribute(String input) {
		input = input.toUpperCase().trim();
		if (input.length() >= 2) {
			String ret = this.toCamelCaseClass(input);
			ret = ret.substring(0, 1).toLowerCase() + ret.substring(1);
			return ret;
		}
		return input.toLowerCase();
	}

	/**
	 * Write the Java class file.
	 * 
	 * @param className
	 * @param sb
	 * @throws IOException
	 */
	private void writeFile(String className, StringBuilder sb) throws IOException {
		File dir = new File(this.outputPathName);

		if (!dir.isDirectory()) {
			dir.mkdirs();
		}

		String fileName = this.outputPathName + File.separator + className + ".java";
		File file = new File(fileName);

		if (file.exists()) {
			file.delete();
		}

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());
		bw.close();
	}

	/**
	 * Inner Class to represent each attribute of class
	 * 
	 * @author Tiago
	 *
	 */
	private class Attribute {
		public String name;
		public boolean pk;
		public String remarks;
		public String type;

		public Attribute(String name, boolean pk, String remarks, String type) {
			super();
			this.name = name;
			this.pk = pk;
			this.remarks = remarks;
			this.type = type;
		}
	}
}
