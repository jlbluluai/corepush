package com.xyz.core.push.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

	/**
	 * 按传入正则分割字符串并存入list
	 * 
	 * @param content
	 * @param regex
	 * @return
	 */
	public static List<String> splitToList(String content, String regex) {
		String[] split = content.split(regex);
		List<String> content_list = new ArrayList<String>();
		for (String s : split) {
			content_list.add(s);
		}
		return content_list;
	}

	public static void main(String[] args) {
		String str = "\"802\",\"20190517\",\"0221\",\"8020221000000000006\",\"\",\"\",\"\",\"802006660\",\"\",\"\",\"20190516\",\"\",\"802100126417522\",\"1\",\"370724199308078298\",\"\",\"N\",\"01\",\"\",\"3060110020000000000\",\"22213001\",\"\",\"\",\"\",\"\",\"06.00000\",\"8.91000\",\"0.53\",\"9.44000\",\"8.91000\",\"0.53\",\"9.44000\",\"Y\",\"0\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"0\",\"N\",\"\",\"\",\"\",\"\",\"\",\"N\",\"\",\"\",\"0\",\"甘肃农信测试账户\",\"A\"";
				System.out.println(splitToList(str, ",").size());
	}
}
