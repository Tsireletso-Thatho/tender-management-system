package util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for date and time operations.
 * Provides methods for date formatting, parsing, and comparison.
 * 
 * Required for: Closing date enforcement, timestamp formatting, and date display.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class DateUtils {
    
    private static final Logger LOGGER = Logger.getLogger(DateUtils.class.getName());
    
    // Date format patterns
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String DATE_TIME_DISPLAY = "dd MMM yyyy, HH:mm";
    public static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SQL_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private DateUtils() {
    }
    
    /**
     * Formats a Timestamp to a user-friendly date string.
     * 
     * @param timestamp the Timestamp to format
     * @return formatted date string (dd/MM/yyyy), or empty string if null
     */
    public static String formatDate(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        return sdf.format(timestamp);
    }
    
    /**
     * Formats a Timestamp to a user-friendly date and time string.
     * 
     * @param timestamp the Timestamp to format
     * @return formatted date-time string (dd/MM/yyyy HH:mm), or empty string if null
     */
    public static String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        return sdf.format(timestamp);
    }
    
    /**
     * Formats a Timestamp to a display-friendly date and time string.
     * 
     * @param timestamp the Timestamp to format
     * @return formatted date-time string (dd MMM yyyy, HH:mm), or empty string if null
     */
    public static String formatDateTimeDisplay(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_DISPLAY, Locale.ENGLISH);
        return sdf.format(timestamp);
    }
    
    /**
     * Parses a date string into a Timestamp.
     * 
     * @param dateStr the date string in format dd/MM/yyyy
     * @return the parsed Timestamp, or null if parsing fails
     */
    public static Timestamp parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(dateStr.trim());
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING, "Failed to parse date: {0}", dateStr);
            return null;
        }
    }
    
    /**
     * Parses a date-time string into a Timestamp.
     * Supports multiple common formats.
     * 
     * @param dateTimeStr the date-time string
     * @return the parsed Timestamp, or null if parsing fails
     */
    public static Timestamp parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = dateTimeStr.trim();
        
        // Define all supported formats
        String[] formats = {
            "dd/MM/yyyy HH:mm",      // 16/04/2026 17:00
            "dd/MM/yyyy HH:mm:ss",   // 16/04/2026 17:00:00
            "dd-MM-yyyy HH:mm",      // 16-04-2026 17:00
            "yyyy-MM-dd HH:mm",      // 2026-04-16 17:00
            "yyyy-MM-dd'T'HH:mm",    // 2026-04-16T17:00
            "yyyy-MM-dd'T'HH:mm:ss", // 2026-04-16T17:00:00
            "MM/dd/yyyy HH:mm",      // 04/16/2026 17:00 (US format)
            "dd/MM/yyyy",            // 16/04/2026 (date only)
            "yyyy-MM-dd"             // 2026-04-16 (date only)
        };
        
        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                sdf.setLenient(false);
                Date parsedDate = sdf.parse(trimmed);
                Timestamp ts = new Timestamp(parsedDate.getTime());
                LOGGER.log(Level.FINE, "Successfully parsed date with format {0}: {1}", 
                           new Object[]{format, dateTimeStr});
                return ts;
            } catch (ParseException e) {
                // Try next format
            }
        }
        
        // Try LocalDateTime as fallback
        try {
            LocalDateTime ldt = LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Timestamp ts = Timestamp.valueOf(ldt);
            LOGGER.log(Level.FINE, "Successfully parsed date with ISO format: {0}", dateTimeStr);
            return ts;
        } catch (Exception e) {
            // Ignore
        }
        
        LOGGER.log(Level.WARNING, "Failed to parse date-time with any format: {0}", dateTimeStr);
        return null;
    }
    
    /**
     * Gets the current timestamp.
     * 
     * @return current Timestamp
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Checks if a timestamp is in the past.
     * 
     * @param timestamp the Timestamp to check
     * @return true if the timestamp is before the current time
     */
    public static boolean isPast(Timestamp timestamp) {
        if (timestamp == null) {
            return false;
        }
        return timestamp.before(getCurrentTimestamp());
    }
    
    /**
     * Checks if a timestamp is in the future.
     * 
     * @param timestamp the Timestamp to check
     * @return true if the timestamp is after the current time
     */
    public static boolean isFuture(Timestamp timestamp) {
        if (timestamp == null) {
            return false;
        }
        return timestamp.after(getCurrentTimestamp());
    }
    
    /**
     * Checks if a deadline has passed (current time is after the deadline).
     * 
     * @param deadline the deadline Timestamp
     * @return true if the deadline has passed
     */
    public static boolean isDeadlinePassed(Timestamp deadline) {
        return isPast(deadline);
    }
    
    /**
     * Compares two timestamps.
     * 
     * @param ts1 first Timestamp
     * @param ts2 second Timestamp
     * @return negative if ts1 < ts2, zero if equal, positive if ts1 > ts2
     */
    public static int compare(Timestamp ts1, Timestamp ts2) {
        if (ts1 == null && ts2 == null) return 0;
        if (ts1 == null) return -1;
        if (ts2 == null) return 1;
        return ts1.compareTo(ts2);
    }
    
    /**
     * Adds days to a timestamp.
     * 
     * @param timestamp the base Timestamp
     * @param days number of days to add
     * @return new Timestamp with days added
     */
    public static Timestamp addDays(Timestamp timestamp, int days) {
        if (timestamp == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.DAY_OF_MONTH, days);
        return new Timestamp(cal.getTimeInMillis());
    }
    
    /**
     * Gets the current year.
     * 
     * @return current year as integer
     */
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
    /**
     * Formats a Timestamp for SQL insertion.
     * 
     * @param timestamp the Timestamp to format
     * @return SQL-formatted timestamp string
     */
    public static String toSqlTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(SQL_TIMESTAMP_FORMAT, Locale.ENGLISH);
        return sdf.format(timestamp);
    }
    
    /**
     * Formats a LocalDateTime to a display string.
     * 
     * @param dateTime the LocalDateTime to format
     * @return formatted string
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_DISPLAY, Locale.ENGLISH);
        return dateTime.format(formatter);
    }
    
    /**
     * Converts LocalDateTime to Timestamp.
     * 
     * @param dateTime the LocalDateTime to convert
     * @return the equivalent Timestamp
     */
    public static Timestamp toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Timestamp.valueOf(dateTime);
    }
    
    /**
     * Converts Timestamp to LocalDateTime.
     * 
     * @param timestamp the Timestamp to convert
     * @return the equivalent LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
    
    /**
     * Gets a human-readable relative time string (e.g., "2 days ago").
     * 
     * @param timestamp the Timestamp to get relative time for
     * @return relative time string
     */
    public static String getRelativeTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        
        long diffMillis = System.currentTimeMillis() - timestamp.getTime();
        long diffSeconds = diffMillis / 1000;
        long diffMinutes = diffSeconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;
        
        if (diffDays > 30) {
            return formatDate(timestamp);
        } else if (diffDays > 0) {
            return diffDays + (diffDays == 1 ? " day ago" : " days ago");
        } else if (diffHours > 0) {
            return diffHours + (diffHours == 1 ? " hour ago" : " hours ago");
        } else if (diffMinutes > 0) {
            return diffMinutes + (diffMinutes == 1 ? " minute ago" : " minutes ago");
        } else {
            return "Just now";
        }
    }
}