package com.xyz.core.push.execute.weather;

import java.util.*;
import java.util.stream.Collectors;

import com.xyz.core.push.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
 */
@Component
@Slf4j
public class WeatherPush {

    @Value("${weather.url}")
    private String httpUrl;

    @Value("${online.day.during.weather}")
    private String sepecifiedTime;

    // 天气推送需要读取的文件目录的统一地址
    private final String filepath = "file/weather/";

    // 是否启用天气推送的开关（未实现）
    private static boolean flag = true;

    /**
     * 查询城市当天的天气并封装到java对象
     *
     * @param city 城市编号
     * @param name 城市名称
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
        Weather weather = (Weather) JSONObject.toBean(jsonObject, Weather.class);
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

        String[] receivers = {mail};

        MailConfigAdditional config = new MailConfigAdditional();
        config.setSubject("天气推送");
        config.setContent(html);
        config.setNickname("小叶子");

        boolean success = MailUtil.sendMail(receivers, MailConfigFactory.getConfig(), config);
        log.info(String.format("=====向订阅者%s,%s发送的推送结果为%s=====", name, mail, success ? "成功" : "失败"));
    }

    @Autowired
    @Lazy
    private WeatherPush weatherPush;

    /**
     * 定时推送
     */
    public void timingPush() {

        List<String> subscribers = FileUtil.fileToList(filepath + "subscriber-solo.txt");

        int groupSize = 100;
        int totalSub = subscribers.size();
        int groupNum = totalSub % groupSize > 0 ? totalSub / groupSize + 1 : totalSub / groupSize;

        log.info(String.format("=====共有%s位订阅者,将启用%s个线程异步工作,每组系数%s=====", totalSub, groupNum, groupSize));

        subscribers.stream().map(s -> {
            String[] tmp = s.split(Constant.COMMA);
            return Subscriber.builder()
                    .mail(tmp[0])
                    .name(tmp[1])
                    .city(tmp[2])
                    .build();
        }).collect(Collectors.toList())
        .stream().forEach(subscriber -> soloMail(subscriber.getCity(), subscriber.getName(), subscriber.getMail()));

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

            list_subscriber.stream().forEach(subscriber -> soloMail(subscriber.getCity(), subscriber.getName(), subscriber.getMail()));
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
    private static List<Subscriber> getSubscribers(List<String> list) {
        return list.stream().map(s -> {
            String[] tmp = s.split(Constant.COMMA);
            return Subscriber.builder()
                    .mail(tmp[0])
                    .name(tmp[1])
                    .city(tmp[2])
                    .build();
        }).collect(Collectors.toList());
    }

}