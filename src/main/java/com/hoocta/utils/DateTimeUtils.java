package com.hoocta.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class DateTimeUtils {

	private static final String FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS";

	public static long toTimestamp(String dateTimeStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER);
		LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
		return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static String toTimePatternStr(long timestamp) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER);
		Instant instant = Instant.ofEpochMilli(timestamp);
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return dateTime.format(formatter);
	}

	public static long getMonthsMargin(int startYear, int startMonth, int endYear, int endMonth) {
		long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.of(startYear, startMonth),
				YearMonth.of(endYear, endMonth));
		return monthsBetween;
	}
	public static long getMonthsMargin(YearMonth start, YearMonth end) {
		long monthsBetween = ChronoUnit.MONTHS.between(start, end);
		return monthsBetween;
	}


	public static void main(String[] args) {
		
	}
}
