package eie.robot.com.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class mDateUtil {
    /**
     * 比较日期大小
     * @param dateStr0
     * @param dateStr1
     * @return
     */
    public static int compareDate(String dateStr0, String dateStr1) {
        Date date1 = convertDateStrToDate(dateStr0, "datetime");
        Date date2 = convertDateStrToDate(dateStr1, "datetime");
        int result = date1.compareTo(date2);
        return result;
    }

    public static String dateAdd(String dateStr, String field, int amount, String pattern) {
        if (dateStr == null) {
            return "";
        }
        Date date = convertDateStrToDate(dateStr, "datetime");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if ("yyyy".equals(field)) {
            calendar.add(Calendar.YEAR, amount);
        } else if ("MM".equals(field)) {
            calendar.add(Calendar.MONTH, amount);
        } else if ("dd".equals(field)) {
            calendar.add(Calendar.DATE, amount);
        }else if ("mm".equals(field)) {
            calendar.add(Calendar.MINUTE, amount);
        }
        date = calendar.getTime();
        dateStr = formatDate(date, pattern);
        return dateStr;
    }

    /**
     * 把日期字符串转换成日期
     * @param dateStr
     * 		日期字符串
     * @param pattern
     * 		"date":日期,
     * 		"datetime":日期和时间
     * @return
     */
    public static Date convertDateStrToDate(String dateStr, String pattern) {
        if (dateStr == null || "".equals(dateStr.trim())) {
            return null;
        }
        if (!dateStr.contains(":")) {
            dateStr += " 00:00:00";
        }

        if ("date".equals(pattern)) {
            pattern = "yyyy-MM-dd";
        } else if ("datetime".equals(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * 格式化日期字符串
     * @param dateStr
     * @param type
     * 		"date":返回日期(yyyy-MM-dd)
     * 		"time":返回时间(HH:mm:ss)
     * 		"datetime":返回日期和时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    public static String formatDate(String dateStr, String type) {
        if (dateStr == null || "".equals(dateStr.trim())) {
            return "";
        }

        DateFormat formatter = DateFormat.getDateInstance();
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate(date, type);
    }

    /**
     * 格式化日期
     * @param date
     * @param pattern
     * 		"date":返回日期(yyyy-MM-dd)
     * 		"time":返回时间(HH:mm:ss)
     * 		"datetime":返回日期和时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }

        if ("date".equals(pattern)) {
            pattern = "yyyy-MM-dd";
        } else if ("time".equals(pattern)) {
            pattern = "HH:mm:ss";
        } else if ("datetime".equals(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }
}
