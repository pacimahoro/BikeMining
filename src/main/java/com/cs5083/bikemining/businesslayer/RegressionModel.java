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

public class RegressionModel implements Predictor {
	private int stationId;
	private Station station;
	private List<StationStatus> hourlyActivity = new ArrayList<StationStatus>();
	
	public String traningFileName;
	public String testingFileName;
	public int testingSize;
	
	// Index of where the training and testing dataset splits.
	public int splitIndex;	
	public static String DEFAULT_DIR = "~/Documents/OU/DataMining/BikeMining";
	public static int PREDICTION_COUNT = 5;
	
	public RegressionModel(int stationId) {
		this.stationId = stationId;
		this.loadStationData();
	}
	
	public void loadStationData(){
		if (this.station == null) {
			try {
				station = DAOManager.getInstance().getStationById(this.stationId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeRegressionData(List<StationStatus> hourlyRecords, String inputFileName){
		// Create writer
		FileWriter writer;
		try {
			writer = new FileWriter(inputFileName);
			Weather w;
			
			// Add title row
			writer.append("datetime");
			writer.append(",");
			writer.append("workingday");
			writer.append(",");
			writer.append("weather");
			writer.append(",");
			writer.append("temp");
			writer.append(",");
			writer.append("humidity");
			writer.append(",");
			writer.append("windspeed");
			writer.append(",");
			writer.append("count");
			writer.append("\n");
			
			// Add data
			for(StationStatus status : hourlyRecords){
				// Format for date
				Date d = new Date(status.getTime().getMillis());
				String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d.getTime());
				
				writer.append(String.format("%s", s));
				writer.append(","); 
				writer.append(String.valueOf(status.isWorkingDay()));
				writer.append(","); 
				
				w = status.getWeather();
				writer.append(String.valueOf(w.getWeatherStatus()));
				writer.append(","); 
				writer.append(String.valueOf(w.getTemperature()));
				writer.append(",");
				writer.append(String.valueOf(w.getHumidity()));
				writer.append(",");
				writer.append(String.valueOf(w.getWindSpeed()));
				writer.append(",");
				
				writer.append(String.valueOf(status.getAvailableBikes()));
				writer.append("\n");
			}
	
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public String buildTrainingModel(Station station){			
		// short circuit
		if(station == null){ return null; }
		
		// 2. Retrieve hourly station activity.
		boolean includeWeatherData = true;
		hourlyActivity = station.getHourlyBikeActivity(includeWeatherData);
		
		// 4. Create input file for the mining task
		String fileName = "src/main/resources/data/train_regression_"+stationId+".csv";
		
		this.writeRegressionData(hourlyActivity, fileName);
		this.traningFileName = fileName;
		return fileName;
	}
	
	@Override
	public PredictionItem predict(Rengine re, int predictionNumber,
			DateTime currentTime) {
		// R Code
		/* *
		 * check R Code with comments, under: src/main/resources/regression.R
		 * */
		
		REXP x;
		// Set the working directory
		re.eval(String.format("setwd('%s')", DEFAULT_DIR), false);

		// Load libraries we will user
		re.eval("library(xts)", false);
		re.eval("library(gbm)", false);
		
		// load data
		re.eval(String.format("train <- read.csv('%s', stringsAsFactors=FALSE)", this.traningFileName), false);
		re.eval("d = dim(train)");
		System.out.println(x=re.eval("d"));
		
		// Do the pre-processing
		re.eval("train$workingday <- factor(train$workingday, c(0,1), ordered=FALSE)", false);
		re.eval("train$weather <- factor(train$weather, c(4,3,2,1), ordered=TRUE)", false);

		// set date time ####
		re.eval("train$datetime <- as.POSIXct(strptime(train$datetime, '%Y-%m-%d %H:%M:%S'))", false);				

		// create the linear model
		re.eval("train.lm <- lm(count ~ ., data=train)");
		RVector fit = re.eval("train.lm").asVector();
		System.out.println("The intercept is " + fit.at(0).asDoubleArray()[0]);
		System.out.println(x=re.eval("train.lm"));
		
		// predict using 500 samples
		re.eval("n <- 500");
		re.eval("i.test <- sample(1:nrow(train), n)");
		re.eval("test.1 = train[i.test, 1:6]");
		// create test target variable
		re.eval("test.1.target = train[i.test, 7]");
		//
		re.eval("test.1.pred <- predict(train.lm, newdata=test.1)");
		
		System.out.println(x = re.eval("summary(test.1.pred)"));
		
		double[] predictions = re.eval("test.1$pred").asDoubleArray();
		
		double[] results = new double[PREDICTION_COUNT];
		for(int i=0; predictions!= null && i<predictions.length && i < PREDICTION_COUNT; i++){
			results[i] = predictions[i];
			System.out.println(i+": "+ predictions[i]);
		}
		
		x = re.eval("(test.1.rmsle <- ((1/n)*sum(log(test.1.pred+1)-log(test.1.target+1))^2)^0.5)");
		System.out.print("rmsle = "+ x.asDouble());
		
		PredictionItem predictionItem = new PredictionItem(results, PREDICTION_COUNT, x.asDouble());
		return predictionItem;
	}
	

	@Override
	public String buildTestModel(Station station, int predictionNumber,
			DateTime startingTime) {
		
		List<StationStatus> filteredTestStatuses = new ArrayList<StationStatus>();
		
		int i = 0;
		boolean splitset = false;
		// TODO: use java collection filters (lambdas) instead of this brute force approach
		for (StationStatus stationStatus : hourlyActivity) {
			if(stationStatus.getTime().isAfter(startingTime.getMillis())){
				filteredTestStatuses.add(stationStatus);
				if (splitset == false) {
					this.splitIndex = i+1;
					splitset = true;
				}
			}
			i++;
		}
		
		String fileName = "src/main/resources/data/test_regression_"+ stationId + ".csv";
		if(filteredTestStatuses.size() > 0){
			this.writeRegressionData(filteredTestStatuses, fileName);
			testingSize = filteredTestStatuses.size();
			testingFileName = fileName;
			return fileName;
		}
		
		
		return null;
	}

	@Override
	public PredictionItem predictWithRandomData(Rengine re,
			int predictionNumber, DateTime currentTime) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
