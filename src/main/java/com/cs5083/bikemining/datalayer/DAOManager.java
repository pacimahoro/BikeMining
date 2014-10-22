package com.cs5083.bikemining.datalayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
		System.out.println("\nExecuting query: " + query);
		ResultSet rset = stmt.executeQuery(query);
		
		Station station = null;
		
		while(rset.next()){
			String name = rset.getString("name");
			int dockcount = rset.getInt("dockcount");
			double lat = rset.getDouble("lat");
			double lon = rset.getDouble("long");
			
			station = new Station(station_id,name, lat, lon, dockcount);
			System.out.println("Name: "+ name + " Dockcount: "+ dockcount);
		}
		
		return station;
	}
	
	/**
	 * Get all arrival trips at a given station
	 * @param stationName the name of the station
	 * @return a list of trips that arrives at this station.
	 */
	public List<Trip> getAllArrivalTripsAtStation(String stationName) throws SQLException{
		openDBConnection();
		stmt = conn.createStatement();
		query = "SELECT * FROM bike_trip WHERE endstation ='"+stationName+"'";
		
		rset = stmt.executeQuery(query);
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
		stmt = conn.createStatement();
		query = "SELECT * FROM bike_trip WHERE startstation ='"+stationName+"'";
		
		rset = stmt.executeQuery(query);
		List<Trip> departures = new ArrayList<Trip>();
		int id, duration, bikeNum;
		Date start, end;
		String endStation = null;
		while (rset.next()){
			id = rset.getInt("trip_id");
			duration = rset.getInt("duration");
			bikeNum = rset.getInt("bikeNumber");
			start = rset.getDate("startdate");
			end = rset.getDate("enddate");
			endStation = rset.getString("endstation");
			
			Trip t = new Trip(id, duration, start, stationName, end, endStation, bikeNum);
			departures.add(t);
			
		}
		System.out.println("departures from dataset: "+ departures.size());
		return departures;
	}
	
	public static DAOManager getInstance(){
		if(instance == null){
			instance = new DAOManager();
		}
		
		return instance;
	}
}
