package com.cs5083.bikemining.datalayer;

import java.util.Date;

import org.joda.time.DateTime;

//import java.util.Date;


/**
 * This class models a Trip object.
 * @author pacifique
 *
 */

public class Trip {
	
	private int id;
	private int duration;
	private Date startDate;
	private Date endDate;
	private String startStation;
	private String endStation;
	private int bikeNumber;
	
	public Trip(int tripId, int duration, Date startDate, String startStation, Date endDate, String endStation, int bikeNumber)
	{
		this.id = tripId;
		this.duration = duration;
		this.startDate = startDate;
		this.startStation = startStation;
		this.endDate = endDate;
		this.endStation = endStation;
		this.bikeNumber = bikeNumber;
	}
	
	public int getDuration(){
		return this.duration;
	}
	
	public String getStartStation(){
		return this.startStation;
	}
	
	public String getEndStation(){
		return this.endStation;
	}
	
	public Date getStartDate(){
		return this.startDate;
	}
	
	public Date getEndDate(){
		return this.endDate;
	}
	
	public int getBikeNumber(){
		return this.bikeNumber;
	}
	
	public int getId(){
		return id;
	}
	
	/**
	 * This function should return the time of the day (hour of the day) of arrival
	 * @return hour of the day
	 */
	public int getArrivalHour(){
		DateTime d = new DateTime(this.endDate); 
		return d.getHourOfDay();
	}
	
	public int getDepartureHour(){
		DateTime d = new DateTime(this.startDate); 
		return d.getHourOfDay();
	}
	
	
	/**
	 * Return the hour that the trip happened. It takes input of the trip type.
	 * @param tripType can either be "departure" or "arrival"
	 * @return hour of the day
	 */
	public int getTripHour(String tripType){
		if(tripType == "departure"){
			return getDepartureHour();
		}
		
		return getArrivalHour();
	}
	
	public int getArrivalDayOfWeek(){
		DateTime d = new DateTime(this.endDate);
		return d.getDayOfWeek();
	}	
}
