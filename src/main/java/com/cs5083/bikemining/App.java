package com.cs5083.bikemining;

import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;

import com.cs5083.bikemining.businesslayer.Coordinator;
import com.cs5083.bikemining.businesslayer.PredictionItem;
import com.cs5083.bikemining.datalayer.DAOManager;
import com.cs5083.bikemining.datalayer.Station;

public class App {
	
	public static void main(String[] args){
		System.out.println("**** Bike Sharing Prediction process started *****");
		Coordinator coordinator = Coordinator.getInstance();
		

		// Get all stations.
		System.out.println("Retrieving all stations data");
		List<Station> stations = Coordinator.getInstance().getStations();

		/**
		 * For now, we will select time that match a given time interval. 
		 * Hopefully at least a day or month or several months.
		 * We will then run our prediction and compare the results with the actual prediction for that time interval.
		 */
		DateTime currentTime = DateTime.now().minusMonths(12);
		
		/*
		 * Run predictions for each stations based on currentTime and prediction count.
		 */
		List<PredictionItem> stationPredictions = coordinator.getAllPredictions(stations, currentTime, 5);
		System.out.println("**** Bike Sharing Prediction process completed *****");
	}
	
	public static String getNormalizedTripHeader(){
		StringBuilder sb = new StringBuilder();
		sb.append("Station");
		sb.append(',');
		String temp;
		for (int i = 0; i < 24; i++) {
			if(i == 23){
				temp = "Pickups 23-0";
			}else{
				temp = "Pickups "+ i +"-"+ (i+1);
			}
			 
			sb.append(temp);
			sb.append(",");
		}
		
		for (int i = 0; i < 24; i++) {
			if(i == 23){
				temp = "Returns 23-0";
			}else{
				temp = "Returns "+ i +"-"+ (i+1);
			}
			 
			sb.append(temp);
			
			if(i!= 23){
				sb.append(",");
			}
		}
		
		return sb.toString();
	}
	
	public static void generateNormalizedTripFile(String fileName){
		DAOManager daoManager = DAOManager.getInstance();
		
		try {
			// Create writer
			FileWriter writer = new FileWriter(fileName);
			List<Station> stations = daoManager.getAllStations();
			
			// Writer the csv header first
			writer.append(getNormalizedTripHeader());
			writer.append("\n");
			
			double[] arr, dep;
			for (Station station : stations) {
				System.out.println("Station Name: "+ station.getName());
				
				// Add Station Id
				writer.append(String.valueOf(station.getId()));
				writer.append(',');
				
				// Add normalized departures
				dep = station.getNormalizedDepartures();
				for (int i = 0; i < dep.length; i++) {
					writer.append(String.valueOf(dep[i]));
					writer.append(',');	
				}
				
				// Add normalized arrivals
				arr = station.getNormalizedArrivals();
				for (int i = 0; i < arr.length; i++) {
					writer.append(String.valueOf(arr[i]));
					if(i != arr.length - 1){
						writer.append(',');	
					}
				}
				
				writer.append('\n');
			}
			
			writer.flush();
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
