package com.xyz.core.push.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

	/**
	 * 按指定时分秒获取带明天日期的日期时间
	 * 
	 * @param time
	 *            HH:mm:ss
	 * @return
	 */
	public static Date getTomSpecifiedTime(String time) {
		time = getTomorrow() + " " + time;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			log.error("日期转换异常", e);
		}

		return date;
	}

	/**
	 * 获取明天日期的字符串
	 * 
	 * @return
	 */
	public static String getTomorrow() {
		return LocalDate.now().minusDays(-1).toString();
	}

	/**
	 * 根据传入时间，计算和现在比较还有多少毫秒，传入时间必须大于当前时间
	 * 
	 * @param date
	 * @return
	 */
	public static long getSpecifiedMS(Date date) {
		Date now = new Date();
		if (!date.after(now)) {
			try {
				throw new Exception("传入时间必须大于当前时间");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		long ms = (date.getTime() - now.getTime()) / 1000;
		return ms;
	}

	public static void main(String[] args) {
		// Date date = getTomSpecifiedTime("08:00:00");
		// System.out.println(date.getTime());
		// Date now = new Date();
		// System.out.println((date.getTime() - now.getTime()) / 1000);

		System.out.println(76988 / 60 / 60);

	}
}
