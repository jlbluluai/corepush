package com.xyz.core.push.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {

	/**
	 * 传入文件所在resources下路径，读取文件，将每一行封装到list中
	 * 
	 * @param filepath
	 * @return
	 */
	public static List<String> fileToList(String filepath) {
		InputStream is = null;
		BufferedReader br = null;
		String line = "";
		List<String> subscribers = new ArrayList<>();

		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filepath);
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				subscribers.add(line);
			}
		} catch (IOException e) {
			log.error("读取文件失败", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("流关闭异常", e);
				}
			}
		}

		return subscribers;
	}

}
