package com.cs5083.bikemining;

import java.sql.SQLException;
import java.util.List;

import com.cs5083.bikemining.datalayer.DAOManager;
import com.cs5083.bikemining.datalayer.Station;
import com.cs5083.bikemining.datalayer.Trip;

public class App {
	public static void main(String[] args){
		System.out.println("Bike Sharing Mining App started");
		
		DAOManager daoManager = DAOManager.getInstance();
		try {
			Station s = daoManager.getStationById(2);

			List<Trip> departures = daoManager.getAllArrivalTripsAtStation(s.getName());			
			for(Trip t : departures){
				System.out.println(t.getStartStation() + ",  " + t.getEndStation()+ "  "+ t.getStartDate());
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
