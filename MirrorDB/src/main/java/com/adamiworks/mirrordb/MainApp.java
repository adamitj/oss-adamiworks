package com.adamiworks.mirrordb;

public class MainApp {

	public static final String APP_NAME = "AdamiWorks MirrorDB";
	public static final String APP_VERSION = "0.0.1-SNAPSHOT";
	public static final String APP_LICENSE = "COPYRIGHT (C) 2015 TIAGO JOSÉ ADAMI - LICENSED UNDER BSD 3-CLAUSE LICENSE";

	//

	public static void main(String[] args) {
		/**
		 * 1st parameter is the name of properties file containing all database
		 * parameters.
		 * 
		 * 2nd parameter is the package name for the classes
		 * 
		 * 3nd parameter is the output directory
		 * 
		 * 4th parameter is the database schema where tables must be mirrored
		 */
		MainApp.printLicense();

		if (args.length < 4) {
			System.out.println("---");
			MainApp.printUsage();
			System.exit(1);
		}

		EntityGenerator gen = new EntityGenerator(args[0], args[1], args[2], args[3]);
		gen.generateEntities();
	}

	private static void printLicense() {
		StringBuilder sb = new StringBuilder();
		sb.append(APP_NAME).append(" v.").append(APP_VERSION).append("\n");
		sb.append(APP_LICENSE).append("\n");
		sb.append("Refer to LICENSE.TXT inside the JAR for further details.");
		System.out.println(sb.toString());
	}

	private static void printUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("This executable program should be used to generate entity classes to mirror\n");
		sb.append("database tables.\n\n\n");
		sb.append("INVALID USAGE. YOU MUST EXECUTE THIS CLASS AS FOLLOWS:\n\n");
		sb.append("   java -jar MirrorDV-x.y.z.jar $db_properties $pgk_name $output_dir $db_schema\n\n");
		sb.append("     $db_properties: Name of Java properties file with db connection params;\n");
		sb.append("     $pkg_name.....: Package name of generated classes;\n");
		sb.append("     $output_dir...: Output directory where classes should be created;\n");
		sb.append("     $db_schema....: Database schema where are the tables to be mirrored;\n\n");
		sb.append("Example:\n");
		sb.append("   java -jar MirrorDV-x.y.z.jar db.properties com.adamiworks.app /home/adw/ent public\n\n");
		System.out.println(sb.toString());
	}

}
