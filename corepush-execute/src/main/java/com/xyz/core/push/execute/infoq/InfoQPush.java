package com.xyz.core.push.execute.infoq;

import com.xyz.core.push.util.HttpUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * InfoQ RSS订阅推送
 *
 * @author xyz
 * @date 2019/4/20
 */
public class InfoQPush {

    // 是否启用InfoQ推送的开关（未实现）
    private static boolean flag = true;

    // InfoQ推送需要读取的文件目录的统一地址
    private final String filepath = "file/infoq/";

    //RSS订阅的地址
    private final String url = "https://www.infoq.cn/feed.xml";

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

    public static void main(String[] args) {
        InfoQPush push = new InfoQPush();
        push.search();
    }

}
