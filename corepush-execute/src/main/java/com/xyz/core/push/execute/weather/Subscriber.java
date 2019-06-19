package com.xyz.core.push.execute.weather;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Subscriber {

	private String mail;

	private String name;

	private String city;

}