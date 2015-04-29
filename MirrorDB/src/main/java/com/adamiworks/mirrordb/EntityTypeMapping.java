package com.adamiworks.mirrordb;

import com.adamiworks.mirrordb.exception.UnknowTypeException;

public class EntityTypeMapping {

	public static String getType(String dbType) throws UnknowTypeException {
		dbType = dbType.toUpperCase().trim();

		if (dbType.equals("TINYINT") || dbType.equals("SMALLINT")) {
			return "Short";
		}

		if (dbType.equals("INT") || dbType.equals("INT4") || dbType.equals("INTEGER") || dbType.equals("SERIAL")) {
			return "Integer";
		}

		if (dbType.equals("BIGINT") || dbType.equals("INT8") || dbType.equals("LONG") || dbType.equals("BIGSERIAL")) {
			return "Long";
		}

		if (dbType.startsWith("DECIMAL") || dbType.startsWith("NUMERIC") || dbType.equals("FLOAT") || dbType.equals("DOUBLE") || dbType.equals("MONEY")
				|| dbType.equals("CURRENCY") || dbType.equals("REAL")) {
			return "Double";
		}

		if (dbType.startsWith("DATE") || dbType.startsWith("TIME") || dbType.startsWith("HOUR")) {
			return "java.util.Date";
		}

		if (dbType.startsWith("BOOL") || dbType.equals("BIT")) {
			return "Boolean";
		}

		if (dbType.startsWith("BLOB") || dbType.startsWith("BYTE") || dbType.equals("LONGBLOB") || dbType.equals("BINARY") || dbType.equals("LONGVARBINARY")) {
			return "byte[]";
		}

		if (dbType.startsWith("CLOB") || dbType.equals("LONGVARCHAR") || dbType.equals("LONGNVARCHAR") || dbType.equals("NCHAR") || dbType.equals("NVARCHAR")
				|| dbType.equals("VARCHAR") || dbType.equals("TEXT")|| dbType.equals("BPCHAR")) {
			return "String";
		}

		throw new UnknowTypeException(dbType);
	}

}
