package backend;

public class DateTime {
    public static final long SERVER_HOUR_OFFSET = 7;
    public static final long COUNTRY_HOUR_OFFSET = 7;
    public static final long MILLISECOND_OFFSET = SERVER_HOUR_OFFSET * 3600000 - COUNTRY_HOUR_OFFSET * 3600000;

    public static long GetCurrentTime() {
        return System.currentTimeMillis() + MILLISECOND_OFFSET;
    }

    public static long GetDayTime(long time) {
        return time % 86400000;
    }
}
