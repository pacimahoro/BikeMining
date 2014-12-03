package com.cs5083.bikemining.businesslayer;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

import com.cs5083.bikemining.datalayer.DAOManager;
import com.cs5083.bikemining.datalayer.Station;
import com.cs5083.bikemining.datalayer.StationStatus;
import com.cs5083.bikemining.datalayer.Weather;

public class Coordinator {

	private StationStatus currentStatus;
	private static Coordinator instance;
	private Rengine re;
	
	private Coordinator() {
		// Create R engine
		String jriArgs[] = {"--no-save"};
		re = new Rengine(jriArgs, false, null);
	 }
	
	public List<PredictionItem> getAllPredictions(List<Station> stations, DateTime predictionTime, int predictionCount){
		List<PredictionItem> preds = new ArrayList<PredictionItem>();
		for (Station station : stations) {
			preds.add(this.runPrediction(station.getId(), predictionTime, predictionCount));
		}
		
		return preds;
	}

	public PredictionItem runPrediction(int stationId, DateTime predictionTime, int predictionCount){	
		try {
			Station station = DAOManager.getInstance().getStationById(stationId);
			getCurrentBikeStatus(stationId);
			
			// Create model
			RegressionModel model = new BoostedRegressionModel(stationId);
			
			// Build training model
			model.buildTrainingModel(station);
			
			// Build testing model
			model.buildTestModel(station, predictionCount, predictionTime);
			
			// Run the prediction
			PredictionItem pred = model.predict(re, predictionCount, predictionTime);
			return pred;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<Station> getStations(){
		try {
			List<Station> stations = DAOManager.getInstance().getAllStations();
			return stations;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public StationStatus getCurrentBikeStatus(int stationId){
		try {
			Station station = DAOManager.getInstance().getStationById(stationId);
			StationStatus currentStatus = station.getCurrentBikeStatus();
			if (currentStatus != null) {
				System.out.println(String.format("Current Time: %s Bike Count: %d", currentStatus.getTime(), currentStatus.getAvailableBikes()));	
			}
			
			return currentStatus;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Coordinator getInstance(){
		if (instance == null) {
			instance = new Coordinator();
		}
		
		return instance;
	}
}
