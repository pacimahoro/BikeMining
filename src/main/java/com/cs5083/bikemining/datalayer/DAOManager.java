package com.cs5083.bikemining.datalayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.joda.time.DateTime;


public class DAOManager {

	
	private static DAOManager instance = null;
	private Connection conn;
	private Statement stmt;
	private String query;
	private ResultSet rset;
	
	public void openDBConnection() throws SQLException{
		if(conn != null && !conn.isClosed()){
			return;
		}
		
		// Create new connection
		/*
		 * Edit this with information PostgreSQL Configuration
		 */
		// TODO: Make this read configuration from a file.
		String url = "jdbc:postgresql://localhost:5433/bikedata";
		Properties props = new Properties();
		props.setProperty("user","postgres");
		props.setProperty("password","test1");
		conn = DriverManager.getConnection(url, props);
	}
	
	/**
	 * Get information about a station given its ID.
	 * @param station_id
	 * @return Station object or NULL.
	 * @throws SQLException
	 */
	public Station getStationById(int station_id) throws SQLException{
		// TODO: Implement function to return a given station.
		openDBConnection();
		stmt = conn.createStatement();
		query = "SELECT * FROM bike_station WHERE station_id="+ station_id;
		ResultSet rset = stmt.executeQuery(query);
		
		Station station = null;
		
		while(rset.next()){
			String name = rset.getString("name");
			int dockcount = rset.getInt("dockcount");
			double lat = rset.getDouble("lat");
			double lon = rset.getDouble("long");
			double lastPrediction = rset.getDouble("prediction");
			Date t = rset.getTimestamp("lastmodified");
			DateTime tt = null;
			if(t != null){
				 tt = new DateTime(t);
			}
					
			station = new Station(station_id,name, lat, lon, dockcount);
			station.setLastPrediction(lastPrediction);
			station.setLastModified(tt);
		}
		
		return station;
	}
	
	public List<Station> getAllStations() throws SQLException{
		openDBConnection();
		query = "SELECT * FROM bike_station";
		PreparedStatement ps = conn.prepareStatement(query);
		rset = ps.executeQuery();
		
		List<Station> stations = new ArrayList<Station>();
		int id, dockcount;
		String name;
		double lat, lon;
		Station s;
		int clusterid;
		while(rset.next()){
			id = rset.getInt("station_id");
			dockcount = rset.getInt("dockcount");
			name = rset.getString("name");
			lat = rset.getDouble("lat");
			lon = rset.getDouble("long");
			clusterid = rset.getInt("clusterid");
			
			double lastPrediction = rset.getDouble("prediction");
			Date t = rset.getTimestamp("lastmodified");
			DateTime tt = null;
			if(t != null){
				 tt = new DateTime(t);
			}
					
			s = new Station(id,name, lat, lon, dockcount);
			s.setLastPrediction(lastPrediction);
			s.setLastModified(tt);
			s.setClusterId(clusterid);
			
			stations.add(s);
		}
		
		return stations;
	}
	
	/**
	 * Helper function to add quotes around db statements. 
	 * @param str
	 * @return
	 */
	private String getQuotedValue(String str){
		return '\''+str+'\'';
	}
	
	/**
	 * Get all arrival trips at a given station
	 * @param stationName the name of the station
	 * @return a list of trips that arrives at this station.
	 */
	public List<Trip> getAllArrivalTripsAtStation(String stationName) throws SQLException{
		openDBConnection();
		query = "SELECT * FROM bike_trip WHERE endstation = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, getQuotedValue(stationName));
		
		rset = ps.executeQuery();
		List<Trip> arrivals = new ArrayList<Trip>();
		while (rset.next()){
			int id = rset.getInt("trip_id");
			int duration = rset.getInt("duration");
			int bikeNum = rset.getInt("bikeNumber");
			Date start = rset.getTimestamp("startdate");
			Date end = rset.getTimestamp("enddate");
			String startStation = rset.getString("startstation");
			
			Trip t = new Trip(id, duration, start, startStation, end, stationName, bikeNum);
			arrivals.add(t);
		}
		
		System.out.println("arrivals from dataset: "+ arrivals.size());
		return arrivals;
	}	

	
	/**
	 * Get all departure trips from a given station
	 * @param station_id
	 * @return a list of all trips that departs from this station.
	 */
	public List<Trip> getAllDepartureTripsFromStation(String stationName) throws SQLException{
		openDBConnection();
		query = "SELECT * FROM bike_trip WHERE startstation =?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, getQuotedValue(stationName));
		
		rset = ps.executeQuery();
		List<Trip> departures = new ArrayList<Trip>();
		int id, duration, bikeNum;
		Date start, end;
		String endStation = null;
		while (rset.next()){
			id = rset.getInt("trip_id");
			duration = rset.getInt("duration");
			bikeNum = rset.getInt("bikeNumber");
			start = rset.getTimestamp("startdate");
			end = rset.getTimestamp("enddate");
			endStation = rset.getString("endstation");
			
			Trip t = new Trip(id, duration, start, stationName, end, endStation, bikeNum);
			departures.add(t);
			
		}
		System.out.println("departures from dataset: "+ departures.size());
		return departures;
	}
	
	public List<StationStatus> getStationBikeAvailability(int stationId) throws SQLException{
		openDBConnection();
		query = "SELECT * FROM bike_hourlystatus WHERE station_id =? ORDER BY rebalancing_time ASC";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, stationId);
		rset = ps.executeQuery();
		
		List<StationStatus> l = new ArrayList<StationStatus>();
		while (rset.next()){
			int bike_available = rset.getInt("bikes_available");
			int docks_available = rset.getInt("docks_available");
			Date t = rset.getTimestamp("rebalancing_time");
			
			StationStatus s = new StationStatus(stationId, bike_available, docks_available, new DateTime(t));
			l.add(s);
		}
		
		return l;
	}
	
	public boolean bulkInsertBikeHourlyStatuses(List<StationStatus> statuses) throws SQLException{
		openDBConnection();
		query = "INSERT INTO bike_hourlystatus VALUES(?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(query);
		
		for (StationStatus status : statuses) {
			ps.setInt(1, status.getStationId());
			ps.setInt(2, status.getAvailableBikes());
			ps.setInt(3, status.getAvailableDocks());
			ps.setTimestamp(4, new java.sql.Timestamp(status.getTime().getMillis()));
			ps.executeUpdate();	
		}	
		return true;
	}
	
	public boolean savePredictionResults(Station station) throws SQLException{
		openDBConnection();
		query = "UPDATE bike_station SET prediction = ?, lastmodified = ? where station_id=?"; 
		PreparedStatement ps = conn.prepareStatement(query);
		double[] preds = station.getPredictions();
		DateTime date = new DateTime();
		ps.setDouble(1, preds[0]);
		ps.setTimestamp(2, new java.sql.Timestamp(date.getMillis()));
		ps.setInt(3, station.getId());
		ps.executeUpdate();	
		
		return true;
	}
	
	public Weather getWeatherData(int stationId, DateTime day) throws SQLException{
		openDBConnection();
		query = "SELECT * FROM weather WHERE wDate =?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setDate(1, new java.sql.Date(day.getMillis()));
		rset = ps.executeQuery();
		
		int i = 0;
		Weather m = null;
		
		// FIXME: for now since we don't have a way to map station (id, or name) to the ZIP code field in the weather data,
		// we will use the first record found as the default.
		while(rset.next() && i == 0){
			int t = rset.getInt("mean_temperature_f");
			int h = rset.getInt("mean_humidity");
			int w = rset.getInt("mean_wind_speed_mph");
			int z = rset.getInt("zip");
			String e = rset.getString("events");
			
			m = new Weather(t, h, w, e, z);
			i++;
		}
		
		return m;
	}
	
	public static DAOManager getInstance(){
		if(instance == null){
			instance = new DAOManager();
		}
		
		return instance;
	}
}
