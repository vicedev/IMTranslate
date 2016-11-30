package com.vice.imtranslate.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class TimeFormatUtils {
        /**
         * 掉此方法输入所要转换的时间输入例如（"2014年06月14日16时09分00秒"）返回时间戳
         *
         * @param time
         * @return
         */
        public String data(String time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒",
                    Locale.CHINA);
            Date date;
            String times = null;
            try {
                date = sdr.parse(time);
                long l = date.getTime();
                String stf = String.valueOf(l);
                times = stf.substring(0, 10);
                Log.d("--444444---", times);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return times;
        }

        /**
         * 掉此方法输入所要转换的时间输入例如（"2014-06-14-16-09-00"）返回时间戳
         *
         * @param time
         * @return
         */
        public String dataOne(String time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                    Locale.CHINA);
            Date date;
            String times = null;
            try {
                date = sdr.parse(time);
                long l = date.getTime();
                String stf = String.valueOf(l);
                times = stf.substring(0, 10);
                Log.d("--444444---", times);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return times;
        }

        public static String getTimestamp(String time, String type) {
            SimpleDateFormat sdr = new SimpleDateFormat(type, Locale.CHINA);
            Date date;
            String times = null;
            try {
                date = sdr.parse(time);
                long l = date.getTime();
                String stf = String.valueOf(l);
                times = stf.substring(0, 10);
                Log.d("--444444---", times);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return times;
        }

        /**
         * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
         *
         * @param time
         * @return
         */
        public String times(String time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
            @SuppressWarnings("unused")
            long lcc = Long.valueOf(time);
            int i = Integer.parseInt(time);
            String times = sdr.format(new Date(i * 1000L));
            return times;

        }
        /**
         * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分"）
         *
         * @param time
         * @return
         */
        public String timet(String time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
            @SuppressWarnings("unused")
            long lcc = Long.valueOf(time);
            int i = Integer.parseInt(time);
            String times = sdr.format(new Date(i * 1000L));
            return times;

        }

        // 调用此方法输入所要转换的时间戳例如（1402733340）输出（"2014年06月14日16时09分00秒"）
        public static String times(long timeStamp) {
            SimpleDateFormat sdr = new SimpleDateFormat("MM月dd日  #  HH:mm");
            return sdr.format(new Date(timeStamp)).replaceAll("#",
                    getWeek(timeStamp));

        }

        private static String getWeek(long timeStamp) {
            int mydate = 0;
            String week = null;
            Calendar cd = Calendar.getInstance();
            cd.setTime(new Date(timeStamp));
            mydate = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
            if (mydate == 1) {
                week = "周日";
            } else if (mydate == 2) {
                week = "周一";
            } else if (mydate == 3) {
                week = "周二";
            } else if (mydate == 4) {
                week = "周三";
            } else if (mydate == 5) {
                week = "周四";
            } else if (mydate == 6) {
                week = "周五";
            } else if (mydate == 7) {
                week = "周六";
            }
            return week;
        }

        // 并用分割符把时间分成时间数组
        /**
         * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14-16-09-00"）
         *
         * @param time
         * @return
         */
        public String timesOne(String time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            @SuppressWarnings("unused")
            long lcc = Long.valueOf(time);
            int i = Integer.parseInt(time);
            String times = sdr.format(new Date(i * 1000L));
            return times;

        }

        /**
         * 并用分割符把时间分成时间数组
         *
         * @param time
         * @return
         */
        public static String[] timestamp(String time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
            @SuppressWarnings("unused")
            long lcc = Long.valueOf(time);
            int i = Integer.parseInt(time);
            String times = sdr.format(new Date(i * 1000L));
            String[] fenge = times.split("[年月日时分秒]");
            return fenge;
        }

        /**
         * 根据传递的类型格式化时间
         *
         * @param str
         * @param type
         *            例如：yy-MM-dd
         * @return
         */
        public static String getDateTimeByMillisecond(String str, String type) {

            Date date = new Date(Long.valueOf(str));

            SimpleDateFormat format = new SimpleDateFormat(type);

            String time = format.format(date);

            return time;
        }

        /**
         * 分割符把时间分成时间数组
         *
         * @param time
         * @return
         */
        public String[] division(String time) {

            String[] fenge = time.split("[年月日时分秒]");

            return fenge;

        }

        /**
         * 输入时间戳变星期
         *
         * @param time
         * @return
         */
        public static String changeweek(String time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
            long lcc = Long.valueOf(time);
            int i = Integer.parseInt(time);
            String times = sdr.format(new Date(i * 1000L));
            Date date = null;
            int mydate = 0;
            String week = null;
            try {
                date = sdr.parse(times);
                Calendar cd = Calendar.getInstance();
                cd.setTime(date);
                mydate = cd.get(Calendar.DAY_OF_WEEK);
                // 获取指定日期转换成星期几
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (mydate == 1) {
                week = "星期日";
            } else if (mydate == 2) {
                week = "星期一";
            } else if (mydate == 3) {
                week = "星期二";
            } else if (mydate == 4) {
                week = "星期三";
            } else if (mydate == 5) {
                week = "星期四";
            } else if (mydate == 6) {
                week = "星期五";
            } else if (mydate == 7) {
                week = "星期六";
            }
            return week;

        }

        /**
         * 获取日期和星期　例如：２０１４－１１－１３　１１:００　星期一
         *
         * @param time
         * @param type
         * @return
         */
        public static String getDateAndWeek(String time, String type) {
            return getDateTimeByMillisecond(time + "000", type) + "  "
                    + changeweekOne(time);
        }

        /**
         * 输入时间戳变星期
         *
         * @param time
         * @return
         */
        public static String changeweekOne(String time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            long lcc = Long.valueOf(time);
            int i = Integer.parseInt(time);
            String times = sdr.format(new Date(i * 1000L));
            Date date = null;
            int mydate = 0;
            String week = null;
            try {
                date = sdr.parse(times);
                Calendar cd = Calendar.getInstance();
                cd.setTime(date);
                mydate = cd.get(Calendar.DAY_OF_WEEK);
                // 获取指定日期转换成星期几
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (mydate == 1) {
                week = "星期日";
            } else if (mydate == 2) {
                week = "星期一";
            } else if (mydate == 3) {
                week = "星期二";
            } else if (mydate == 4) {
                week = "星期三";
            } else if (mydate == 5) {
                week = "星期四";
            } else if (mydate == 6) {
                week = "星期五";
            } else if (mydate == 7) {
                week = "星期六";
            }
            return week;

        }

        /**
         * 获取当前时间
         *
         * @return
         */
        public static String getCurrentTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分");
            return sdf.format(new java.util.Date());
        }

        /**
         * 输入日期如（2014年06月14日16时09分00秒）返回（星期数）
         *
         * @param time
         * @return
         */
        public String week(String time) {
            Date date = null;
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
            int mydate = 0;
            String week = null;
            try {
                date = sdr.parse(time);
                Calendar cd = Calendar.getInstance();
                cd.setTime(date);
                mydate = cd.get(Calendar.DAY_OF_WEEK);
                // 获取指定日期转换成星期几
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (mydate == 1) {
                week = "星期日";
            } else if (mydate == 2) {
                week = "星期一";
            } else if (mydate == 3) {
                week = "星期二";
            } else if (mydate == 4) {
                week = "星期三";
            } else if (mydate == 5) {
                week = "星期四";
            } else if (mydate == 6) {
                week = "星期五";
            } else if (mydate == 7) {
                week = "星期六";
            }
            return week;
        }

        /**
         * 输入日期如（2014-06-14-16-09-00）返回（星期数）
         *
         * @param time
         * @return
         */
        public String weekOne(String time) {
            Date date = null;
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            int mydate = 0;
            String week = null;
            try {
                date = sdr.parse(time);
                Calendar cd = Calendar.getInstance();
                cd.setTime(date);
                mydate = cd.get(Calendar.DAY_OF_WEEK);
                // 获取指定日期转换成星期几
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (mydate == 1) {
                week = "星期日";
            } else if (mydate == 2) {
                week = "星期一";
            } else if (mydate == 3) {
                week = "星期二";
            } else if (mydate == 4) {
                week = "星期三";
            } else if (mydate == 5) {
                week = "星期四";
            } else if (mydate == 6) {
                week = "星期五";
            } else if (mydate == 7) {
                week = "星期六";
            }
            return week;
        }

    //字符串转时间戳
    public static String getTime(String timeString){
        String timeStamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        Date d;
        try{
            d = sdf.parse(timeString);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch(ParseException e){
            e.printStackTrace();
        }
        return timeStamp;
    }

    //时间戳转字符串
    public static String getStrTime(String timeStamp){
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        long  l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }
    }
