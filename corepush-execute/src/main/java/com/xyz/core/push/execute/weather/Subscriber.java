package com.xyz.core.push.execute.weather;

public class Subscriber {

	private String mail;

	private String name;

	private String city;

	@Override
	public String toString() {
		return "Subscriber [mail=" + mail + ", name=" + name + ", city=" + city + "]";
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}