package com.adamiworks.utils;

public class OSUtils {

	private String OS;
	private static OSUtils self;

	private OSUtils() {
		this.OS = System.getProperty("os.name").toLowerCase();
	}

	public boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}

	public String getOS() {
		if (isWindows()) {
			return "win";
		} else if (isMac()) {
			return "osx";
		} else if (isUnix()) {
			return "uni";
		} else if (isSolaris()) {
			return "sol";
		} else {
			return "err";
		}
	}

	public static OSUtils currentInstance() {
		if (self == null) {
			self = new OSUtils();
			System.out.println("Current Operating System: " + self.OS);
		}
		return self;
	}
}
