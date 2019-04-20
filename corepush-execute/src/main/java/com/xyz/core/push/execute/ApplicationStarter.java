package com.xyz.core.push.execute;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.xyz.core.push.execute.weather.WeatherPush;

@SpringBootApplication
@ComponentScan("com.xyz.core.push.execute")
@ComponentScan("com.xyz.core.push.util")
@Component
public class ApplicationStarter{

	@Autowired
	private WeatherPush weatherPush;

	private static ApplicationStarter factory;

	@PostConstruct
	public void init() {
		factory = this;
	}

	public static void main(String[] args) {
		SpringApplication.run(ApplicationStarter.class, args);
		System.out.println("服务启动成功......");

		factory.weatherPush.schedule();
	}

//	@Override
//	public void run(String... args) throws Exception {
//		weatherPush.schedule();
//	}
}
