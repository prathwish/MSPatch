package com.patch.patch_management.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class TimeZoneConversionUtil
{

	private static final String				DATE_FORMAT	= "yyyy-MM-dd hh:mm";
	private static final DateTimeFormatter	formatter	= DateTimeFormatter.ofPattern(DATE_FORMAT);

	public static String timeZoneConversion(String zone, String date, String time)
	{
		ZoneId toTimeZone = ZoneId.of("Asia/Kolkata");
		String placeStr = null;
		Set<String> zoneIds = ZoneId.getAvailableZoneIds();

		for (String zoneId : zoneIds)
		{
			placeStr = zoneId;
			boolean result = placeStr.contains(zone);
			if (result)
				break;
		}
		ZoneId fromTimeZone = ZoneId.of(placeStr);

		String[] values = date.split("-");
		int year = Integer.parseInt(values[0]);
		int month = Integer.parseInt(values[1]);
		int day = Integer.parseInt(values[2]);

		values = time.split(":");
		int hour = Integer.parseInt(values[0]);
		int mins = Integer.parseInt(values[1]);

		ZonedDateTime inDST = ZonedDateTime.of(year, month, day, hour, mins, 0, 0, fromTimeZone);

		ZonedDateTime currentETime = inDST;

		ZonedDateTime currentISTime = currentETime.withZoneSameInstant(toTimeZone);

		return formatter.format(currentISTime);
	}

	public static String calculateDate(Integer week, String day)
	{
		LocalDate d = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
		TemporalAccessor accessor = formatter.parse(day);
		LocalDate d2 = d.with(TemporalAdjusters.dayOfWeekInMonth(week, DayOfWeek.from(accessor)));

		LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());

		return d2.toString();

	}
}
