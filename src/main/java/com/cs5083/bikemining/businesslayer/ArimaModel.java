package com.cs5083.bikemining.businesslayer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.rosuda.JRI.Rengine;

import com.cs5083.bikemining.datalayer.Station;
import com.cs5083.bikemining.datalayer.StationStatus;

public class ArimaModel implements Predictor {

	public ArimaModel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String buildTrainingModel(Station station) {
		// TODO Auto-generated method stub
		// short circuit
		if(station == null){ return null; }
		
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
			
			return fileName;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public PredictionItem predict(Rengine rengine, int predictionNumber,
			DateTime currentTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildTestModel(Station station, int predictionNumber,
			DateTime startingTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
