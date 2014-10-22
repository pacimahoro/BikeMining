package com.cs5083.bikemining.datalayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.RowFilter.Entry;

/**
 * This class implements a model object for a Bike Station
 * @author pacifique
 *
 */
public class Station {

	private int id;
	private String name;
	private double latitude;
	private double longitude;
	private int dockcount;
	private String landmark;
	private String installation;
	
	public Station(int station_id, String name, double latitude, double longitude, int dockcount, String landmark, String installation){
		this.id = station_id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dockcount = dockcount;
		this.landmark = landmark;
		this.installation = installation;
	}
	
	public Station(int station_id, String name, double latitude, double longitude, int dockcount){
		this.id = station_id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dockcount = dockcount;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setLatitude(double lat) {
		this.latitude = lat;
	}
	
	public double getLatitude() {
		return this.latitude;
	}
	
	public void setLongitude(double lon) {
		this.longitude = lon;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public void setDockcount(int dockcount){
		this.dockcount = dockcount;
	}
	
	public int getDockcount(){
		return this.dockcount;
	}
	
	private List<Trip> filterWeekDayTrips(List<Trip> allTrips){
		List<Trip> results = new ArrayList<Trip>();
		
		for(Trip t : allTrips){
			if(t.getArrivalDayOfWeek() <= 5){
				results.add(t);
			}
		}
		return results;
	}
	
	private double[] normalizeTripCounts(List<Trip> trips, String tripType){
		// Total number of trips 
		int totalCount = trips.size();
		
		// Group the trips per hour
		int [] arrivalHoursCount = new int[24];
		for(Trip t : trips){
			int index = t.getTripHour(tripType);
			int c = arrivalHoursCount[index];
			c++;
			arrivalHoursCount[index] = c;
		}
		
		// Normalize the count
		double [] normalizedHourlyTrips = new double[24];
		
		for (int i = 0; i < normalizedHourlyTrips.length; i++) {
			double v = (double)arrivalHoursCount[i]/totalCount;
			normalizedHourlyTrips[i] = v;
		}
		return normalizedHourlyTrips;
	}
	
	public double[] getNormalizedArrivals(){
		try {
			// Get all arrivals
			List<Trip> allArrivals = DAOManager.getInstance().getAllArrivalTripsAtStation(this.getName());
			
			// Filter weekdays only 
			List<Trip> weekdayArrivals = this.filterWeekDayTrips(allArrivals);
			
			return normalizeTripCounts(weekdayArrivals, "arrival");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
	}
	
	public double[] getNormalizedDepartures(){
		try {
			// Get all arrivals
			List<Trip> departures = DAOManager.getInstance().getAllDepartureTripsFromStation(this.getName());
			
			// Filter weekdays only 
			List<Trip> weekdayDepartures = this.filterWeekDayTrips(departures);
			System.out.println("weekday departures: "+ weekdayDepartures.size());
			
			for (Trip i : weekdayDepartures) {
				System.out.println("StartStation: "+ i.getStartStation()+", EndStation: "+ i.getEndStation()+ ", Start: "+i.getStartDate());
			}
			
			return normalizeTripCounts(weekdayDepartures, "departure");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
