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
	
	public Coordinator() {
	}
	
	private void prepareArimaData(Station station){
		// short circuit
		if(station == null){ return; }
		
		// 2. Retrieve hourly station activity.
		boolean includeWeatherData = false;
		List<StationStatus> hourlyActivity = station.getHourlyBikeActivity(includeWeatherData);
		
		// 3. build the bikeCount series
		List<Integer> bikeCountHourlySeries = new ArrayList<Integer>();
		for (StationStatus stationStatus : hourlyActivity) {
			bikeCountHourlySeries.add(stationStatus.getAvailableBikes());
		}
		
		// 4. Create input file for the mining task
		String fileName = "input_arima_"+station.getId()+".csv";
		
		// Create writer
		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
			for(int bikeCount : bikeCountHourlySeries){
				writer.append(String.valueOf(bikeCount));
				writer.append("\n");
			}
	
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void runPrediction(int stationId, DateTime predictionTime, int predictionCount){	
		try {
			Station station = DAOManager.getInstance().getStationById(stationId);
			
			// Create R engine
			String jriArgs[] = {"--no-save"};
			Rengine re = new Rengine(jriArgs, false, null);
			
			// Create model
			RegressionModel model = new BoostedRegressionModel(stationId);
			
			// Build training model
			model.buildTrainingModel(station);
			
			// Build testing model
			model.buildTestModel(station, 5, predictionTime);
			
			// Run the prediction
			PredictionItem pred = model.predict(re, 5, predictionTime);
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
