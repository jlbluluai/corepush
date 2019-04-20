package com.xyz.core.push.execute.weather;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.xyz.core.push.util.DateUtil;
import com.xyz.core.push.util.FileUtil;
import com.xyz.core.push.util.HttpUtils;
import com.xyz.core.push.util.PropertiesUtil;
import com.xyz.core.push.util.StringUtil;
import com.xyz.core.push.util.mail.MailConfigAdditional;
import com.xyz.core.push.util.mail.MailConfigFactory;
import com.xyz.core.push.util.mail.MailUtil;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 天气推送
 * 
 * @author xyz
 *
 */
@Component
@Slf4j
public class WeatherPush {

	// 从中国天气网获取json字符串的http地址（未加城市编号）
	private final String httpUrl = "http://t.weather.sojson.com/api/weather/city/";

	// 天气推送需要读取的文件目录的统一地址
	private final String filepath = "file/weather/";

	// 是否启用天气推送的开关（未实现）
	private static boolean flag = true;

	/**
	 * 查询城市当天的天气并封装到java对象
	 * 
	 * @param city
	 *            城市编号
	 * @param name
	 *            城市名称
	 * @return
	 */
	private Weather searchToday(String city, String name) {
		String s = HttpUtils.get(String.format(httpUrl + city));

		Properties prop = PropertiesUtil.getProperties(filepath + "city.properties");
		city = (String) prop.get(city);

		JSONObject jsonObject = JSONObject.fromObject(s);
		jsonObject = (JSONObject) jsonObject.get("data");
		String result = jsonObject.getString("forecast");
		JSONArray jsonArray = JSONArray.fromObject(result);
		result = jsonArray.getString(0);
		jsonObject = JSONObject.fromObject(result);
		Weather weather = (Weather) jsonObject.toBean(jsonObject, Weather.class);
		weather.setCity(city);
		weather.setName(name);

		return weather;
	}

	/**
	 * 生成天气预报的html
	 * 
	 * @param weather
	 * @return
	 */
	private String generateHtml(Weather weather) {
		String city = weather.getCity();
		String high = weather.getHigh();
		String low = weather.getLow();
		String type = weather.getType();
		String ymd = weather.getYmd();
		String week = weather.getWeek();
		String fx = weather.getFx();
		String fl = weather.getFl();
		String notice = weather.getNotice();
		String name = weather.getName();

		String html = "<!DOCTYPE html><html>	<head>		<meta charset='UTF-8'>	</head>	<body>		<table>			<tbody>				<tr>					<td>尊敬的订阅者"
				+ name + "</td>				</tr>				<tr>					<td>" + ymd + city
				+ "天气推送</td>				</tr>				<tr>					<td>" + week
				+ "</td>				</tr>				<tr>					<td>今日天气：</td>					<td>"
				+ high + " " + low
				+ "</td>				</tr>				<tr>					<td>今日状态：</td>					<td>"
				+ type
				+ "</td>				</tr>				<tr>					<td>今日风向：</td>					<td>"
				+ fx + " " + fl
				+ "</td>				</tr>				<tr>					<td>小叶子温馨提示：</td>					<td>"
				+ notice + "</td>				</tr>			</tbody>		</table>	</body></html>";

		return html;
	}

	/**
	 * 由于邮件内容不统一，目前只做单点推送，即循环调用该方法，后续考虑引入多线程
	 * 
	 * @param city
	 * @param name
	 * @param mail
	 */
	private void soloMail(String city, String name, String mail) {
		Weather weather = searchToday(city, name);
		String html = generateHtml(weather);

		String[] receivers = { mail };

		MailConfigAdditional config = new MailConfigAdditional();
		config.setSubject("天气推送");
		config.setContent(html);
		config.setNickname("小叶子");

		boolean success = MailUtil.sendMail(receivers, MailConfigFactory.getConfig(), config);
		log.info(String.format("=====向订阅者%s,%s发送的推送结果为%s=====", name, mail, success ? "成功" : "失败"));
	}

	/**
	 * 对所有的订阅者天气推送
	 */
	private void massMail() {
		List<String> subscribers = FileUtil.fileToList(filepath + "subscriber-solo.txt");
		log.info(String.format("=====共有%s位订阅者=====", subscribers.size()));

		List<Subscriber> list_subscriber = getSubscribers(subscribers);
		for (Subscriber subscriber : list_subscriber) {
			soloMail(subscriber.getCity(), subscriber.getName(), subscriber.getMail());
		}
	}

	@Value("${online.day.during.weather}")
	private String sepecifiedTime;

	/**
	 * 定时推送，每次上线定为次日指定时间为首次推送时间（单线程工作）
	 */
	public void timingService() {
		// String time = "2019-03-22 15:31:50";
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date date = null;
		// try {
		// date = sdf.parse(time);
		// System.out.println(date);
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }

		Date date = DateUtil.getTomSpecifiedTime(sepecifiedTime);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				massMail();
			}
		}, date, 1000 * 60 * 60 * 24);
	}

	@Autowired
	@Lazy
	private WeatherPush weatherPush;

	/**
	 * 定时推送，多线程工作
	 */
	public void schedule() {

		// String time = "2019-03-25 11:29:40";
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date date = null;
		// try {
		// date = sdf.parse(time);
		// // System.out.println(date);
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }

		Date date = DateUtil.getTomSpecifiedTime(sepecifiedTime);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				List<String> subscribers = FileUtil.fileToList(filepath + "subscriber-solo.txt");
				int a = 1;// 分组数
				int size = subscribers.size();
				if (size <= 5) {
					a = 1;
				} else if (size <= 10) {
					a = 2;
				} else if (size <= 15) {
					a = 3;
				} else if (size <= 20) {
					a = 4;
				} else {
					a = 5;
				}
				// 每组系数
				int factor = (size % a == 0) ? size / a : size / a + 1;

				log.info(String.format("=====共有%s位订阅者,将启用%s个线程异步工作,每组系数%s=====", subscribers.size(), a, factor));

				for (int i = 0; i < a; i++) {
					List<String> temp = new ArrayList<>();

					if (i < a - 1) {
						for (int j = 0; j < factor; j++) {
							temp.add(subscribers.get(j + factor * i));
						}
					} else {
						for (int j = 0; j < size - (a - 1) * factor; j++) {
							temp.add(subscribers.get(j + factor * i));
						}
					}
					weatherPush.executeAsync(temp);
				}
			}
		}, date, 1000 * 60 * 60 * 24);

	}

	/**
	 * 多线程开始执行
	 * 
	 * @param list
	 */
	@Async("asyncServiceExecutor")
	private void executeAsync(List<String> list) {
		log.info(String.format("=====线程%s开始工作=====", Thread.currentThread().getName()));
		try {
			List<Subscriber> list_subscriber = getSubscribers(list);
			for (Subscriber subscriber : list_subscriber) {
				soloMail(subscriber.getCity(), subscriber.getName(), subscriber.getMail());
			}

			// 相隔一天
			// long ms = DateUtil.getSpecifiedMS(DateUtil.getTomSpecifiedTime("08:00:00"));
			// Thread.sleep(ms);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info(String.format("=====线程%s结束工作=====", Thread.currentThread().getName()));
	}

	/**
	 * 传入字符串的list，传出封装好Subscriber对象的list
	 * 
	 * @param list
	 * @return
	 */
	private List<Subscriber> getSubscribers(List<String> list) {
		List<Subscriber> list_subscriber = new ArrayList<>();
		for (String s : list) {
			List<String> oneSub = StringUtil.splitToList(s, ",");
			Subscriber subscriber = new Subscriber();
			subscriber.setMail(oneSub.get(0));
			subscriber.setName(oneSub.get(1));
			subscriber.setCity(oneSub.get(2));
			list_subscriber.add(subscriber);
		}
		return list_subscriber;
	}

	/* 订阅者更新模块，待开发 */
	/*
	 * 思路：由ftp控制更新，由更新端上传更新后名单以及更新标志文件到ftp，
	 * 每天推送前检测ftp是否有更新标志，有则先更新至本地，并删去更新标识，无则直接推送
	 * 
	 */
}