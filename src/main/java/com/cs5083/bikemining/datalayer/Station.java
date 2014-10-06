package com.cs5083.bikemining.datalayer;

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
	
}
