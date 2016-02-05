package com.adamiworks.filesync;

/**
 *
 * @author Tiago J. Adami
 */
public class Main {
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		// File w = new File("D:\\");
		// String d[] = FileUtils.getDirList(w);
		//
		// for (String s : d) {
		// System.out.println(s);
		// }

		boolean showInfo = false;

		if (args.length == 3 && !args[2].toLowerCase().equals("secure")) {
			showInfo = true;
		}

		if (args.length > 3 || args.length < 2) {
			showInfo = true;
		}

		if (showInfo) {
			System.out.println("");
			System.out.println("FileSync 2 - Copyright 2016 (c) Tiago J. Adami - http://www.adamiworks.com");
			System.out.println(
					"THIS PROGRAM IS DISTRIBUTED UNDER GNU GPLv3 LICENCE. READ LICENSE.TXT FOR FURTHER DETAILS.");
			System.out.println("");
			System.out.println("   Usage for secure method (slow, uses MD5 hash)");
			System.out.println("      java -jar FileSync.jar <source dir> <destination dir> [secure] [verbose]");
			System.out.println("");
			System.out.println("   Usage for fast method:");
			System.out.println("      java -jar FileSync.jar <source dir> <destination dir>");
			System.out.println("");
		} else {
			FileSync fs = null;
			
			boolean secure = (args.length > 2 && args[2].toLowerCase().equals("secure"))
					|| (args.length > 3 && args[3].toLowerCase().equals("secure"));
			
			boolean verbose = (args.length > 2 && args[2].toLowerCase().equals("verbose"))
					|| (args.length > 3 && args[3].toLowerCase().equals("verbose"));

			fs = new FileSync(secure, verbose);

			fs.syncFolder(args[0], args[1]);
			fs.showFileCount(true);
		}
	}
}
