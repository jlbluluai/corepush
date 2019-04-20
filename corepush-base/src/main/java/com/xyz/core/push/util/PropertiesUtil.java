package com.xyz.core.push.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesUtil {

	/**
	 * 传入属性文件存放的地址（resources目录下开始算），读取属性文件
	 * 
	 * @param filepath
	 * @return
	 */
	public static Properties getProperties(String filepath) {
		InputStream is = null;
		Properties props = new Properties();
		String temp;

		is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filepath);
		try {
			if (is != null) {
				is = new BufferedInputStream(is);
			} else {
				is = new BufferedInputStream(new FileInputStream(filepath));
			}
			props.load(is);
			is.close();
		} catch (IOException e) {
			log.error("加载属性文件出错", e);
		}
		return props;
	}

	public static void main(String[] args) {
		System.out.println(PropertiesUtil.class.getClassLoader().getResource(""));
		String prop = "file/weather/city.properties";
		System.out.println(prop);
		Properties properties = getProperties(prop);
		System.out.println(properties.get("101210101"));
	}
}
