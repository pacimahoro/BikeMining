package com.cs5083.bikemining.datalayer;

import java.sql.SQLException;

import org.joda.time.DateTime;

public class StationStatus {

	private int station_id; 
	private int availableBikes;
	private int availableDocks;
	private DateTime time;
	private Weather weather;
	
	public StationStatus(int station_id, int availableBikes, int availableDocks, DateTime statusTime) {
		this.station_id = station_id;
		this.availableBikes = availableBikes;
		this.availableDocks = availableDocks;
		this.time = statusTime;
	}
	
	public int getAvailableBikes(){
		return this.availableBikes;
	}
	
	public int getAvailableDocks(){
		return this.availableDocks;
	}
	
	public DateTime getTime(){
		return this.time;
	}
	
	public Weather getWeather(){
		return this.weather;
	}
	
	public void setWeather(Weather w){
		this.weather = w;
	}
	
	public int getStationId(){
		return this.station_id;
	}
	
	public int isWorkingDay(){
		if (this.time.getDayOfWeek() <=5){
			return 1;
		}
		return 0;
	}
}
