package com.cs5083.bikemining.datalayer;

/**
 * This class implements a model object for a Bike Station
 * @author pacifique
 *
 */
public class Station {

	private int id;
	private String name;
	private String latitude;
	private String longitude;
	private int dockcount;
	private String landmark;
	private String installation;
	
	public Station(int station_id, String name, String latitude, String longitude, int dockcount, String landmark, String installation){
		this.id = station_id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dockcount = dockcount;
		this.landmark = landmark;
		this.installation = installation;
	}
	
	public Station(int station_id, String name, String latitude, String longitude, int dockcount){
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
	
	public void setLatitude(String lat) {
		this.latitude = lat;
	}
	
	public String getLatitude() {
		return this.latitude;
	}
	
	public void setLongitude(String lon) {
		this.longitude = lon;
	}
	
	public String getLongitude(){
		return this.longitude;
	}
	
	public void setDockcount(int dockcount){
		this.dockcount = dockcount;
	}
	
	public int getDockcount(){
		return this.dockcount;
	}
	
}
