package com.xyz.core.push.execute;

import com.xyz.core.push.execute.infoq.InfoQPush;
import com.xyz.core.push.execute.weather.WeatherPush;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan({"com.xyz.core.push.execute",
        "com.xyz.core.push.util"})
@PropertySource("classpath:config.properties")
@ImportResource({"classpath:applicationContext-quartz.xml"})
@Component
public class ApplicationStarter {

    @Autowired
    private WeatherPush weatherPush;

    @Autowired
    private InfoQPush infoQPush;

    private static ApplicationStarter factory;

    @PostConstruct
    public void init() {
        factory = this;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
        System.out.println("服务启动成功......");

//        factory.weatherPush.schedule();
//        factory.infoQPush.schedule();
    }

//	@Override
//	public void run(String... args) throws Exception {
//		weatherPush.schedule();
//	}
}
