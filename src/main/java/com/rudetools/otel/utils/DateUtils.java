/**
 * 
 */
package com.rudetools.otel.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

/**
 * @author James Schneider
 *
 */
public class DateUtils {

	/**
	 * 
	 */
	public DateUtils() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
		
			
			//Calendar cal = Calendar.getInstance();
			
			//cal.roll(Calendar.MONTH, -1);
			//cal.set(Calendar.DAY_OF_MONTH, 12);
			
			// 2018-12-12 23:30:24
			// SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
			
			LocalDateTime fromTime = LocalDateTime.parse("2024-03-21T12:30:24");
			LocalDateTime toTime = LocalDateTime.parse("2024-03-31T15:30:24");
			
			
			
			//System.out.println("From = " + fromTime);
			//System.out.println("To   = " + toTime);
			
			//long hours = ChronoUnit.HOURS.between(fromTime, toTime);
			
			long hours = differenceInHours("2024-03-31T12:30:24", "2024-03-31T15:30:24");
			
			System.out.println("Hours = " + hours);
			
			
			//System.out.println(getLastLogin());
			//System.out.println(getNextPaymentDate("mortgage"));
			//System.out.println(getNextPaymentDate("auto"));
			
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		
	}

	
	
	// expected date-time format is "dd-MMM-yyyy HH:mm:ss" or "dd-MMM-yyyyTHH:mm:ss"
	public static long differenceInHours(String fromStr, String toStr) throws Throwable {
		
		String convertedFrom = StringUtils.replaceAll(fromStr, " ", "T");
		String convertedTo = StringUtils.replaceAll(toStr, " ", "T");
		
		LocalDateTime fromTime = LocalDateTime.parse(convertedFrom);
		LocalDateTime toTime = LocalDateTime.parse(convertedTo);

		return ChronoUnit.HOURS.between(fromTime, toTime);
		
	}
	
	
	public static String getNextPaymentDate(String paymentType) {
	
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (paymentType.equals("mortgage")) {
			cal.roll(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			return df.format(cal.getTime());
		} else {
			if (day > 17) {
				cal.roll(Calendar.MONTH, 1);
				cal.set(Calendar.DAY_OF_MONTH, 17);
				return df.format(cal.getTime());
			} else {
				cal.set(Calendar.DAY_OF_MONTH, 17);
				return df.format(cal.getTime());				
			}
			
		}
		
	}
	
	public static int getPaymentDayOfMonth(String paymentType) {
		if (paymentType.equals("mortgage")) {
			return 1;
		} else {
			return 17;
		}
		
	}
	
	public static String getLastLogin() {
		
		Calendar cal = Calendar.getInstance();
		
		cal.roll(Calendar.DAY_OF_MONTH, false);
		cal.roll(Calendar.HOUR_OF_DAY, true);
		cal.roll(Calendar.MINUTE, 3);
		
		return cal.getTime().toString();
	}
}
