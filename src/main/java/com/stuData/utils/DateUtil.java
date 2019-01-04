package com.stuData.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

    /**
     * 一周星期数组
     */
    private final static String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
    /**
     * 一周星期数组
     */
    private final static String dayNums[] = { "7", "1", "2", "3", "4", "5", "6" };

    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String dtLong = "yyyyMMddHHmmss";

    /**
     * 获取日期指定的格式的字符串
     * 
     * @param date
     * @param pattern
     * @return Date 对象类型字符串形式
     */
    public static String formatDate(Date date, String pattern) {
        if (null != date) {
            DateFormat dateFomat = new SimpleDateFormat(pattern);
            return dateFomat.format(date);
        }
        return "";
    }

    /**
     * 字符串转换为日期
     * 
     * @throws java.text.ParseException
     */
    public static Date getStringToDate(String date, String pattern) {
        if (null == date) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 新旧格式转换
     * 
     * @param date
     * @param oldFormat
     * @param newFormat
     * @return
     */
    public static String formatDate(String date, String oldFormat, String newFormat) {
        try {
            Date time = new SimpleDateFormat(oldFormat).parse(date);
            return new SimpleDateFormat(newFormat).format(time);
        } catch (ParseException e) {
            return null;
        }

    }

    /**
     * 获取指定日期所在的周期中的序号，从0开始（当前日期的下一天为周期的起始日期，7天为一个周期，即一周）
     * 
     * @param date
     * @return int
     */
    public static int getIndexInCycle(String date) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_YMD);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 1); // 第二天
            int d = cal.get(Calendar.DAY_OF_WEEK); // 星期几
            cal.setTime(df.parse(date));
            int id = cal.get(Calendar.DAY_OF_WEEK); // 输入日期的星期几
            int idx = id - d;
            idx = idx < 0 ? idx + 7 : idx; // 一周有7天
            return idx;
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 计算两个日期的差距(天数)，去掉时分秒
     * 
     * @param date1
     * @param date2
     * @return Long
     */
    public static Long getDaysToLong(Date date1, Date date2) {
        Long d1 = getYmdTime(date1).getTime() / 1000;
        Long d2 = getYmdTime(date2).getTime() / 1000;
        return Math.abs((d2 - d1)) / (24 * 3600);
    }

    public static int getDaysToInt(Date date1, Date date2) {
        return Integer.valueOf(getDaysToLong(date1, date2).toString());
    }

    /**
     * 去掉时分秒
     * 
     * @param date
     * @return Date
     */
    public static Date getYmdTime(Date date) {
        if (date == null) {
            return (new Date());
        }
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        Date convertTime = day.getTime();
        return convertTime;
    }
    
    /**
     * 计算两个时间,毫秒
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getMillisecond(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }

    // 时间描述 1天是 24*3600*1000毫秒
    public static String getTimeDesc(Date date) {
        if (null == date) {
            return null;
        }
        Calendar now = Calendar.getInstance();
        Calendar time = Calendar.getInstance();
        time.setTime(date);
        // 毫秒偏移量
        long t = (now.getTimeInMillis() - time.getTimeInMillis()) / 1000;
        // 昨天以前的，直接显示日期，如2012-01-08，如果有多余空间，显示日期及时间2012-01-08 09:20
        if ((t < 0) || (t > ((now.get(Calendar.HOUR_OF_DAY) + 24) * 3600 + now.get(Calendar.MINUTE) + 60
                + now.get(Calendar.SECOND))) || (t < 0)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            return df.format(date);
        }

        // 1个小时以上，但还在今天内，显示为"今天 09:30"
        if (t > 3600) {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String str = df.format(date);
            int day_time = time.get(Calendar.DAY_OF_YEAR);// 评论的天
            int now_time = now.get(Calendar.DAY_OF_YEAR);// 当前天

            // 1个小时以上，但在昨天内，显示为"昨天11:59"
            if (day_time < now_time) {
                str = "昨天 ".concat(str);
            } else {
                str = "今天 ".concat(str);
            }
            return str;
        }
        // 1分钟到1个小时内，显示为"XX分钟前"
        if (t >= 60) {
            return t / 60 + "分钟前";
        }
        // 小于1分钟的为刚刚
        return "刚刚";
    }

    /**
     * 根据日期获得星期几
     * 
     * @param date
     * @return
     */
    public static String getWeekDayString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayNames[dayOfWeek - 1];
    }

    /**
     * 根据日期获得星期几 数字
     * 
     * @param date
     * @return
     */
    public static String getWeekDayNumString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayNums[dayOfWeek - 1];
    }

    /**
     * 在当前日基础增加天数
     * 
     * @param amount
     * @return Date
     */
    public static Date addDay(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTime();
    }

    /**
     * 时间差比较 返回毫秒数
     * 
     * @param t1
     * @param t2
     * @return
     */
    public static Long compareTimes(String t1, String t2, String format) {
        DateFormat df = new SimpleDateFormat(format);
        try {
            Date d1 = df.parse(t1);
            Date d2 = df.parse(t2);
            return d1.getTime() - d2.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断t1是否在t2和t3之间
     * 
     * @param t1
     * @param t2
     * @param t3
     * @param format
     * @param flag 是否在区间内
     * @return
     */
    public static Boolean isBetweenTimes(String t1, String t2, String t3, String format, Boolean flag) {
        DateFormat df = new SimpleDateFormat(format);
        try {
            Date d1 = df.parse(t1);
            Date d2 = df.parse(t2);
            Date d3 = df.parse(t3);
            Long c1 = d1.getTime() - d2.getTime();
            Long c2 = d1.getTime() - d3.getTime();
            if (flag) {
                if (c1 >= 0 && c2 <= 0) {
                    return true;
                }
                return false;
            } else {
                if (c1 > 0 && c2 < 0) {
                    return true;
                }
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 判断时间是否在接下来的一天之内
     * @param date
     * @return
     */
    public static boolean isIN24Hour(Date date) {
        Calendar dayNow = Calendar.getInstance();
        if (date.before(dayNow.getTime())) {
            return false;
        }
        dayNow.add(Calendar.DATE, 1);
        if (date.after(dayNow.getTime())) {
            return false;
        } else {
            return true;
        }
    }

    public static Date addMonth(Date date,int n){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(calendar.MONTH, n);
    	return calendar.getTime();
    }
}
