package com.cs5083.bikemining;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.cs5083.bikemining.datalayer.DAOManager;
import com.cs5083.bikemining.datalayer.Station;
import com.cs5083.bikemining.datalayer.Trip;

public class App {
	public static void main(String[] args){
		System.out.println("Bike Sharing Mining App started");
		
		DAOManager daoManager = DAOManager.getInstance();
		try {
			Station s = daoManager.getStationById(2);

//			List<Trip> list  = daoManager.getAllDepartureTripsFromStation(s.getName());
//			System.out.println("Trip Count: "+ list.size());
//			System.out.println(list.get(0).getEndDate());
//			
//			for(Trip i : list){
//				System.out.println("StartStation: "+ i.getStartStation()+", EndStation: "+ i.getEndStation()+ ", Duration: "+i.getDuration());
//			}
			
			System.out.println("Station Name: "+ s.getName());

			System.out.println("Arrivals");
			double[] m = s.getNormalizedArrivals();
			for (int i = 0; i < m.length; i++) {
				System.out.println("Hour: "+i+", N="+m[i]);
			}
			
			System.out.println("Departures");
			m = s.getNormalizedDepartures();
			for (int i = 0; i < m.length; i++) {
				System.out.println("Hour: "+i+", N="+m[i]);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
