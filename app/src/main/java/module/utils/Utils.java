package module.utils;

import module.config.ConfigWago;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Utils {
    // private static TimeZone timeZone = TimeZone.getTimeZone("Singapore");
    private static Random rand = new Random();

    public Utils(){

    }

    public static String getRandomIntegerBetweenRange() {

        return String.format("%03d", rand.nextInt(999));
    }

    public static String getCurrentDateFormat(String dateFormat) {
        // SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        return new SimpleDateFormat(dateFormat).format(Calendar.getInstance(TimeZone.getTimeZone("Singapore")).getTime());
    }


    public static boolean checkDateWithInTolerance(Date d, int maxTolerance) {
        boolean bResult = false;

        // Date curDate = new Date();
        long differentSec = (new Date().getTime() / 1000) - (d.getTime() / 1000);

        if (differentSec <= maxTolerance) {
            bResult = true;
        }

        return bResult;
    }

    public static boolean checkDateWithLastSend(Date d, int ackTolerance) {
        // Date curDate = new Date();
        long differentSec = (new Date().getTime() / 1000) - (d.getTime() / 1000);

        return (differentSec > ackTolerance);
    }
}
