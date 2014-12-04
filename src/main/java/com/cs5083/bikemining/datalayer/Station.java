package com.cs5083.bikemining.datalayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import com.cs5083.bikemining.businesslayer.PredictionItem;

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
	private List<StationStatus> hourlyStatuses;
	private Weather weather;
	private int clusterId;
	private double[] predictions;
	private double lastPrediction;
	private DateTime lastModified;
	
//	public static void main(String[] args){
//		try {
//			List<Station> stations = DAOManager.getInstance().getAllStations();
//			for (Station station : stations) {
//				station.retrieveHourlyActivity();	
//				DAOManager.getInstance().bulkInsertBikeHourlyStatuses(station.getHourlyStatuses());
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
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
	
	public void setClusterId(int clusterid){
		this.clusterId = clusterid;
	}
	
	public int getClusterId(){
		return this.clusterId;
	}
	
	public void setLastPrediction(double p){
		this.lastPrediction = p;
	}
	
	public double getLastPrediction(){
		return this.lastPrediction;
	}
	
	public void setLastModified(DateTime t){
		this.lastModified = t;
	}
	
	public DateTime getLastModified(){
		return this.lastModified;
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
	
	public List<StationStatus> getHourlyStatuses(){
		return this.hourlyStatuses;
	}
	
	public void retrieveHourlyActivity(){
		try {
			if(this.hourlyStatuses == null){
				// Get all status. These are 2-minutes interval status times. 
				this.hourlyStatuses = DAOManager.getInstance().getStationBikeAvailability(this.getId());				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<StationStatus> getHourlyBikeActivity(boolean includeWeatherData){
		if(this.hourlyStatuses == null){
			this.retrieveHourlyActivity();
		}
		
		// Check if we need to include weather data or not.
		if (includeWeatherData == true) {
			this.retrieveWeatherData();	
		}
		
		return this.hourlyStatuses;
	}
	
	public StationStatus getCurrentBikeStatus(){
		if(this.hourlyStatuses == null){
			this.retrieveHourlyActivity();
		}
		
		DateTime t;
		DateTime currentTime = DateTime.now().minusMonths(12);
		for (StationStatus status : this.hourlyStatuses) {
			t = status.getTime();
			if (Hours.hoursBetween(currentTime, t).getHours() == 1){
				return status;
			}
		}
		return null;
	}
	
	public boolean savePredictionResults(PredictionItem item){
		// Save the prediction results
		this.predictions = item.getResults();
		try {
			DAOManager.getInstance().savePredictionResults(this);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public double[] getPredictions(){
		return this.predictions;
	}
	
	public void retrieveWeatherData(){
		Map<String, Weather> dateToWeatherMap = new HashMap<String, Weather>();
		Weather w;
		
		for (StationStatus status : this.hourlyStatuses) {
			try {
				w = dateToWeatherMap.get(status.getTime().toString());
				if(w != null){
					status.setWeather(w);
				}
				else {
					w = DAOManager.getInstance().getWeatherData(this.getId(), status.getTime());
					if(w != null){
						status.setWeather(w);
						dateToWeatherMap.put(status.getTime().toString(), w);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			System.out.println("weekday arrivals: "+ weekdayArrivals.size());
			
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
			
			return normalizeTripCounts(weekdayDepartures, "departure");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
