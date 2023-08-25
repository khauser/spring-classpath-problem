package org.demo.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeService
{
    private DateTimeService() {
        throw new AssertionError();
    }
    /**
     * UTC date time
     */
    public static ZonedDateTime now()
    {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    public static String nowAsString()
    {
        return createString(now());
    }

    private static String createString(ZonedDateTime zonedDateTime)
    {
        if (zonedDateTime != null)
        {
            return zonedDateTime.format(DateTimeFormatter.ofPattern(JsonSerializer.JSON_ZONED_DATE_TIME));
        }
        return "";
    }

    /**
     * 21.09.2018 -> 210918
     */
    public static String createDateStringNow()
    {
        ZonedDateTime now = DateTimeService.now();
        return createDateString(now);
    }

    public static String createDateString(ZonedDateTime dateTime)
    {
        return String.format("%02d%02d%02d", dateTime.getDayOfMonth(), dateTime.getMonthValue(),
                        dateTime.getYear() - 2000);
    }

    public static ZonedDateTime parseDateString(String datePart)
    {
        int day = Integer.parseInt(datePart.substring(0, 2));
        int month = Integer.parseInt(datePart.substring(2, 4));
        int year = Integer.parseInt(datePart.substring(4, 6)) + 2000;

        return ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC);
    }
}
