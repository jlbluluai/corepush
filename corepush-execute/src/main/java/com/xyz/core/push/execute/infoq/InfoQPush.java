package com.xyz.core.push.execute.infoq;

import com.xyz.core.push.util.DateUtil;
import com.xyz.core.push.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * InfoQ RSS订阅推送
 *
 * @author xyz
 * @date 2019/4/20
 */
@Component
@Slf4j
public class InfoQPush {

    // 是否启用InfoQ推送的开关（未实现）
    private static boolean flag = true;

    // InfoQ推送需要读取的文件目录的统一地址
    private static String filepath = "file/infoq/";

    //RSS订阅的地址
    private static String url = "https://www.infoq.cn/feed.xml";

    //定时器
    private ScheduledExecutorService executorService;

    /**
     * 根据rss地址获取并将符合的数据封装到list中
     *
     * @return
     */
    public List<InfoQ> search() {
        String s = HttpUtils.get(url);
        //将String转xml
        StringReader sr = null;
        InputSource is = null;
        Document doc = null;
        try {
            sr = new StringReader(s);
            is = new InputSource(sr);
            doc = (new SAXBuilder()).build(is);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sr != null) {
                sr.close();
            }
        }

        //将当天的记录封装到一个list中
        List<InfoQ> infoqs = new ArrayList<>();
        Element root = doc.getRootElement();
        Element e1 = root.getChild("channel");
        List<Element> elements = e1.getChildren();
        for (Element element : elements) {
            if ("item".equals(element.getName())) {
                Date pubDate = new Date(element.getChild("pubDate").getContent(0).getValue());
                Date nowDate = new Date();
                String pub = new SimpleDateFormat("yyyy-MM-dd").format(pubDate);
                String now = new SimpleDateFormat("yyyy-MM-dd").format(nowDate);
                if (now.equals(pub)) {
                    InfoQ infoQ = new InfoQ();
                    infoQ.setTitle(element.getChild("title").getContent(0).getValue());
                    infoQ.setLink(element.getChild("link").getContent(0).getValue());
                    infoQ.setDescription(element.getChild("description").getContent(0).getValue());
                    infoQ.setPubDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pubDate));
                    infoqs.add(infoQ);
                }
            }
        }

        return infoqs;
    }

    /**
     * 定时推送（并发）
     */
    public void schedule() {
        executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
//        executorService = Executors.newScheduledThreadPool(1);

        long initialDelay = DateUtil.getSpecifiedMS(DateUtil.getTomSpecifiedTime("08:00:00"));//初始延迟
        System.out.println(initialDelay);
        initialDelay = 0;
        long period = 1000 * 10;//间隔延迟
        TimeUnit unit = TimeUnit.MILLISECONDS;//时间单位

        Future future = executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info(String.format("=====线程%s开始工作=====", Thread.currentThread().getName()));
            }
        }, initialDelay, period, unit);

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //executorService关闭模板
        /*try {
            executorService.shutdown();
            if (!executorService.awaitTermination(1000 * 10, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("awaitTermination interrupted: " + e);
            executorService.shutdownNow();
        }*/
    }

    public static void main(String[] args) {
        InfoQPush push = new InfoQPush();
//        push.search();

        push.schedule();
    }

}
