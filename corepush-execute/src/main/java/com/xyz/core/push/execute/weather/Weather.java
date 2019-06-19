package com.xyz.core.push.execute.weather;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Weather {

	private String city;

	private String high;

	private String low;

	private String type;

	private String ymd;

	private String week;

	private String fx;

	private String fl;

	private String notice;

	private String name;

	private String date;

	private String sunrise;

	private String sunset;

	private String aqi;

}