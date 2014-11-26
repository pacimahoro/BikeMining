package com.cs5083.bikemining.datalayer;

public class Weather {
	
	private int temperature;
	private int humidity;
	private int windSpeed;
	private boolean isRain;
	private int zip;
	private String event;
	
	public Weather(int temperature, int humidity, int windSoeed, String event, int zip) {
		this.temperature = temperature;
		this.humidity = humidity;
		this.windSpeed = windSoeed;
		this.event = event;
		this.zip = zip;
		if(this.event != null){
			System.out.println("event: " + this.event);
		}
	}
	
	public int getTemperature(){
		return this.temperature;
	}
	
	public int getHumidity(){
		return this.humidity;
	}
	
	public int getWindSpeed(){
		return this.windSpeed;
	}
	
	public boolean isRain(){
		return this.isRain;
	}
	
	public int getWeatherStatus(){
		int status = 1;
		
		if (this.event == null || this.event.isEmpty()){
			status = 1;
		}
		else if ( this.event.equals("Fog")) {
			status = 2;
		} 
		else if ( this.event.equals("Rain") || this.event.equals("rain")) {
			status = 3;
		}
		else if ( this.event.equals("Thunderstorm") ||this.event.equals("Fog-Rain")){
			status = 4;
		}
		
		return status;
	}
	
	public int ZipCode(){
		return this.zip;
	}
	
	public String getEvent(){
		return this.event;
	}

}
