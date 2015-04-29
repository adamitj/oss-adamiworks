package com.adamiworks.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

	/**
	 * Facade method to format a date into a String.
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * Sum <code>days</code> to the <code>date</code>. Negative
	 * <code>days</code> number subtract the number of days from
	 * <code>date</code>.
	 * 
	 * @param date
	 *            The current date
	 * @param days
	 *            The number of days to sum
	 * @return The new date after adding days
	 */
	public static Date daysAfter(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	/**
	 * Returns the number of days between d1 and d2.
	 * 
	 * @param d1
	 *            The lowest date on the range
	 * @param d2
	 *            The highest date on the range
	 * @return The number of days between d1 and d2
	 */
	public static Long dateDiff(Date d1, Date d2) {
		long diff = d2.getTime() - d1.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	/**
	 * Checks if a date period overlaps another at least in one day.
	 * 
	 * @param startD1
	 * @param endD1
	 * @param startD2
	 * @param endD2
	 * @return
	 */
	public static boolean conflicts(Date startD1, Date endD1, Date startD2, Date endD2) {
		Calendar calStartD1 = Calendar.getInstance();
		Calendar calEndD1 = Calendar.getInstance();
		Calendar calStartD2 = Calendar.getInstance();
		Calendar calEndD2 = Calendar.getInstance();

		calStartD1.setTime(startD1);
		calEndD1.setTime(endD1);
		calStartD2.setTime(startD2);
		calEndD2.setTime(endD2);

		return DateUtils.conflicts(calStartD1, calEndD1, calStartD2, calEndD2);
	}

	public static boolean conflicts(Calendar calStartD1, Calendar calEndD1, Calendar calStartD2, Calendar calEndD2) {
		if (calStartD1.getTimeInMillis() >= calStartD2.getTimeInMillis() && calStartD1.getTimeInMillis() < calEndD2.getTimeInMillis()) {
			// If start1 is between start2 and end2
			return true;
		}

		if (calEndD1.getTimeInMillis() > calStartD2.getTimeInMillis() && calEndD1.getTimeInMillis() <= calEndD2.getTimeInMillis()) {
			// If start1 is between start2 and end2
			return true;
		}

		return false;
	}

	/**
	 * Merge two Date variables to compose a single Calendar object.
	 * 
	 * @param date
	 *            Date object containing the date - doesn't matter hour, minute,
	 *            second and millisseconds values.
	 * @param hourMinuteMillisseconds
	 *            Date object containing hour, minute, second and millisseconds.
	 *            Doesn't matter date values.
	 * @return a Calendar object
	 */
	public static final Calendar getCalendarMergingDates(Date date, Date hourMinuteMillisseconds) {
		Calendar ret = Calendar.getInstance();
		ret.setTime(date);

		Calendar hour = Calendar.getInstance();
		hour.setTime(hourMinuteMillisseconds);

		ret.set(Calendar.HOUR_OF_DAY, hour.get(Calendar.HOUR_OF_DAY));
		ret.set(Calendar.MINUTE, hour.get(Calendar.MINUTE));
		ret.set(Calendar.SECOND, hour.get(Calendar.SECOND));
		ret.set(Calendar.MILLISECOND, hour.get(Calendar.MILLISECOND));

		return ret;
	}

}
