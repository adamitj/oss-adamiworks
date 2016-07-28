/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adamiworks.filesync;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.adamiworks.filesync.exception.InvalidDirectoryException;
import com.adamiworks.filesync.exception.WritePermissionDeniedException;
import com.adamiworks.utils.FileUtils;
import com.adamiworks.utils.StringUtils;

/**
 * Synchronizes a file into another location.<BR>
 * There are two methods to check if the sync is necessary: Fast and Secure:
 * <b>Fast Method</b>: the fastest way to check changes in the files. This
 * method determine if a sync is needed comparing two attributes: file length
 * and file modification date. If the source file doesn't exists in destination,
 * the file is synchronized;<BR>
 * <b>Secure Method</b>: the secure method uses Fast Method to identify all the
 * files to be synchronized. For all files in which Fast Method don't apply
 * (when modification date and length don't change), a MD5 hash is calculated
 * for source and for destination file. If hash value is different, the file is
 * synchronized.
 * 
 * @author Tiago J. Adami
 */
public final class FileSync extends Thread {

	private static long fileCount = 0;
	private boolean secureMethodOn;
	private boolean verbose;
	private static int threadCount = 0;
	private String threadSourceDir;
	private String threadDestinationDir;

	private void init() {
		secureMethodOn = false;
	}

	public FileSync() {
		this.init();
	}

	public FileSync(boolean secureMethodOn, boolean verbose) {
		this.init();
		FileSync.addThreadCount();
		this.secureMethodOn = secureMethodOn;
		this.verbose = verbose;
	}

	public static synchronized void addThreadCount() {
		threadCount++;
	}

	public static synchronized void removeThreadCount() {
		threadCount--;
	}

	public static synchronized int getThreadCount() {
		return threadCount;
	}

	/**
	 * Set parameters to be used in a FileSync thread.
	 * 
	 * @param threadSourceDir
	 *            Source directory
	 * @param threadDestinationDir
	 *            Destination directory
	 */
	private void setThreadParameters(String threadSourceDir, String threadDestinationDir) {
		this.threadDestinationDir = threadDestinationDir;
		this.threadSourceDir = threadSourceDir;
	}

	/**
	 * Check attributes to determine if source and destination file are
	 * different (are sync-able)
	 * 
	 * @param s
	 *            Source path and file name.
	 * @param d
	 *            Destination path and file name.
	 * @return true if files are different or if destination file does not
	 *         exists; false if are equals.
	 */
	public boolean isSyncAble(String s, String d) {
		File dest = new File(d);

		// Check for destination file existence
		if (!dest.exists()) {
			return true;
		}

		File src = new File(s);

		// Compare length
		if (src.length() != dest.length()) {
			return true;
		}

		// Compare modified date
		if (src.lastModified() != dest.lastModified()) {
			return true;
		}

		// Check if secure method is on. If yes, compare MD5 hashes to certify
		// the file will be sync-ed for any changes.
		if (secureMethodOn) {
			try {
				if (verbose)
					Logger.getLogger(FileUtils.class.getName()).log(Level.INFO, "Calculating MD5 for " + s);
				// Message.info(" Calculating MD5 Hash for " + s);
				String srcMd5 = FileUtils.getHashMD5(src);

				if (verbose)
					Logger.getLogger(FileUtils.class.getName()).log(Level.INFO, "Calculating MD5 for " + d);
				// Message.info(" Calculating MD5 Hash for " + d);
				String destMd5 = FileUtils.getHashMD5(dest);

				if (!srcMd5.equals(destMd5)) {
					if (verbose)
						Logger.getLogger(FileUtils.class.getName()).log(Level.INFO,
								"MD5 Hash differs " + srcMd5 + " != " + srcMd5);
					return true;
				}
			} catch (Exception ex) {
				Logger.getLogger(FileSync.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return false;
	}

	/**
	 * Sync a file from a source path to a destination path
	 * 
	 * @param s
	 *            Source path and file name.
	 * @param d
	 *            Destination path and file name.
	 * @return
	 */
	public boolean syncFile(String s, String d) {
		File src = new File(s);
		File dest = new File(d);

		if (this.isSyncAble(s, d)) {
			if (dest.exists()) {
				dest.delete();

				if (dest.exists()) {
					Logger.getLogger(FileUtils.class.getName()).log(Level.WARNING,
							"File " + d + " could not be replaced. Check file permissions and try again.");

				}
			}

			FileUtils.fileCopy(src, dest);
			return true;
		} else {
			if (verbose)
				Logger.getLogger(FileUtils.class.getName()).log(Level.INFO, "File " + d + " is up to date.");
		}

		return false;
	}

	/**
	 * Synchronize a source folder to a destination path.
	 * 
	 * @param sourceParentFolder
	 *            the parent folder containing all files to be synchronized.
	 * @param destinationParentFolder
	 *            the destination when the files must be updated.
	 */
	public void syncFolder(String sourceParentFolder, String destinationParentFolder) {
		Vector<String> folders = new Vector<String>();
		Vector<String> files = new Vector<String>();

		if (sourceParentFolder == null || sourceParentFolder.trim().equals("")) {
			Logger.getLogger(FileSync.class.getName()).log(Level.SEVERE, "Invalid source path!");
			return;
		}

		if (destinationParentFolder == null || destinationParentFolder.trim().equals("")) {
			Logger.getLogger(FileSync.class.getName()).log(Level.SEVERE, "Invalid destination path!");
			return;
		}

		if (!StringUtils.right(sourceParentFolder, 1).equals(File.separator)) {
			sourceParentFolder += File.separator;
		}

		// sourceParentFolder = sourceParentFolder.replaceAll("\\\\", "/");
		// destinationParentFolder = destinationParentFolder.replaceAll("\\\\",
		// "/");

		File sourceDir = new File(sourceParentFolder);
		File destinationDir = new File(destinationParentFolder);

		if (!sourceDir.exists()) {
			Logger.getLogger(FileSync.class.getName()).log(Level.SEVERE,
					"Source path \"" + sourceParentFolder + "\" does not exists or is not a directory!");
			return;
		}

		// Check if destination directory exists
		if (!destinationDir.exists()) {
			if (!destinationDir.mkdirs()) {
				try {
					throw new WritePermissionDeniedException(destinationParentFolder);
				} catch (WritePermissionDeniedException ex) {
					Logger.getLogger(FileSync.class.getName()).log(Level.SEVERE, null, ex);
				}
				return;
			}
		}

		// Check if destination directory is really a directory
		if (!destinationDir.isDirectory()) {
			try {
				throw new InvalidDirectoryException(sourceParentFolder);
			} catch (InvalidDirectoryException ex) {
				Logger.getLogger(FileSync.class.getName()).log(Level.SEVERE, null, ex);
			}
			return;
		}

		if (sourceDir.isDirectory()) {
			try {
				Logger.getLogger(FileSync.class.getName()).log(Level.FINE,
						"Syncing DIR \"" + sourceParentFolder + "\" to \"" + destinationParentFolder + "\"");
				String[] result = FileUtils.getDirList(sourceDir, false);

				Arrays.sort(result);

				// Find all files and folders inside directory
				if (result != null && result.length > 0) {
					for (String s : result) {
						File test = new File(sourceParentFolder + s);
						if (test.isDirectory()) {
							folders.add(s);
						} else {
							files.add(s);
						}
					}

					if (!sourceParentFolder.trim().endsWith(File.separator)) {
						sourceParentFolder = sourceParentFolder.trim() + File.separator;
					}
					if (!destinationParentFolder.trim().endsWith(File.separator)) {
						destinationParentFolder = destinationParentFolder.trim() + File.separator;
					}

					for (String s : folders) {
						if (FileSync.getThreadCount() < 1) {
							FileSync fs = new FileSync(secureMethodOn, verbose);
							fs.setThreadParameters(sourceParentFolder + s, destinationParentFolder + s);
							fs.start();
						} else {
							this.syncFolder(sourceParentFolder + s, destinationParentFolder + s);
						}
					}

					// Process all files inside the directory
					for (String s : files) {
						String sourceFileName = sourceParentFolder + s;
						String destinationFileName = destinationParentFolder + s;

						// Logger.getLogger(FileSync.class.getName()).log(Level.INFO,
						// "Syncing FILE [" + sourceFileName + "] to [" +
						// destinationFileName + "]");

						// Sync file
						if (this.syncFile(sourceFileName, destinationFileName)) {
							// System.out.println("Syncing FILE [" +
							// sourceFileName + "] to [" + destinationFileName +
							// "]");
							System.out.println(destinationFileName);
							fileCount++;
						}
					}
					if (verbose)
						Logger.getLogger(FileSync.class.getName()).log(Level.INFO,
								files.size() + " files processed in directory " + sourceParentFolder);

				}
			} catch (Exception ex) {
				Logger.getLogger(FileSync.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			try {
				throw new InvalidDirectoryException(sourceParentFolder);
			} catch (InvalidDirectoryException ex) {
				Logger.getLogger(FileSync.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Show the number o files synchronized.
	 * 
	 * @param resetCount
	 *            reset the file counter to 0.
	 */
	public void showFileCount(boolean resetCount) {
		System.out.println(String.valueOf(fileCount) + " files synchronized.");
		if (resetCount) {
			fileCount = 0;
		}
	}

	@Override
	public void run() {
		this.syncFolder(threadSourceDir, threadDestinationDir);
		FileSync.removeThreadCount();
	}
}
