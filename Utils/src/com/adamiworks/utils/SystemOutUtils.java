package com.adamiworks.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SystemOutUtils {

	private boolean enabled = false;

	public SystemOutUtils(boolean enabled) {
		super();
		this.enabled = enabled;
	}

	/**
	 * Simply write a timestamp value for log purporses into System.out.
	 * 
	 * @param message
	 */
	public void printTimestamp(String message) {
		if (enabled) {
			Date date = Calendar.getInstance().getTime();
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.SSS");

			if (message != null && message.trim().length() > 0) {
				System.out.println(sdf.format(date) + ": " + message);
			} else {
				System.out.println(sdf.format(date));
			}

		}
	}

	boolean isEnabled() {
		return enabled;
	}

	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
