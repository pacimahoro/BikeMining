package com.cs5083.bikemining.businesslayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;
import org.springframework.core.io.ClassPathResource;

import com.cs5083.bikemining.datalayer.DAOManager;
import com.cs5083.bikemining.datalayer.Station;
import com.cs5083.bikemining.datalayer.StationStatus;
import com.cs5083.bikemining.datalayer.Weather;

public class Prediction {

	private int stationId;
	public Prediction(int stationId) {
		this.stationId = stationId;
		
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
		String fileName = "input_arima_"+stationId+".csv";
		
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
	
	private void prepareRegressionModelData(Station station){			
		// short circuit
		if(station == null){ return; }
		
		// 2. Retrieve hourly station activity.
		boolean includeWeatherData = true;
		List<StationStatus> hourlyActivity = station.getHourlyBikeActivity(includeWeatherData);
		
		// 4. Create input file for the mining task
		String fileName = "input_regression_"+stationId+".csv";
		
		// Create writer
		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
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
			for(StationStatus status : hourlyActivity){
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
	
	public void prepareData(){
		Station station = null;
		try {
			station = DAOManager.getInstance().getStationById(this.stationId);
			if(station != null){
				this.prepareRegressionModelData(station);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void Predict(){
//		this.prepareData();
	}
	
	public void runRScriptForLinearModelPrediction(Rengine re){
		// R Code
		/* *
		 * check R Code with comments, under: src/main/resources/regression.R
		 * */
		
		REXP x;
		// Set the working directory
		re.eval("setwd('~/Documents/OU/DataMining/BikeMining')", false);

		// Load libraries we will user
		re.eval("library(xts)", false);
		re.eval("library(gbm)", false);
		
		// load data
		re.eval("train <- read.csv('input_regression_2.csv', stringsAsFactors=FALSE)", false);
		re.eval("d = dim(train)");
		System.out.println(x=re.eval("d"));
		
		// Do the pre-processing
		re.eval("train$workingday <- factor(train$workingday, c(0,1), ordered=FALSE)", false);
		re.eval("train$weather <- factor(train$weather, c(4,3,2,1), ordered=TRUE)", false);

		// set date time ####
		re.eval("train$datetime <- as.POSIXct(strptime(train$datetime, '%Y-%m-%d %H:%M:%S'))", false);
//		System.out.println(x=re.eval("head(train)"));
//		

//		// create the linear model
		re.eval("train.lm <- lm(count ~ ., data=train)");
		RVector fit = re.eval("train.lm").asVector();
		System.out.println("The intercept is " + fit.at(0).asDoubleArray()[0]);
		System.out.println(x=re.eval("train.lm"));
		
//		String FILE_PATH = "input_regression_2.csv";
//		File inputFilePath = new File(FILE_PATH);
//		re.eval(String.format("trainingDataPath <- '%s'", inputFilePath.getAbsolutePath()));

		
		// predict using 500 samples
		re.eval("n <- 500");
		re.eval("i.test <- sample(1:nrow(train), n)");
		re.eval("test.1 = train[i.test, 1:6]");
		// create test target variable
		re.eval("test.1.target = train[i.test, 7]");
		//
		re.eval("test.1.pred <- predict(train.lm, newdata=test.1)");
		//
		System.out.println(x = re.eval("summary(test.1.pred)"));
		
		System.out.print("rmsle = ");
		System.out.println(x=re.eval("(test.1.rmsle <- ((1/n)*sum(log(test.1.pred+1)-log(test.1.target+1))^2)^0.5)"));
		
	}
	
	public void runRScriptForBoostedPrediction(Rengine re)
	{
		// R Code
		/* *
		 * check R Code with comments, under: src/main/resources/regression.R
		 * */
		
		REXP x;
		// Set the working directory
		re.eval("setwd('~/Documents/OU/DataMining/BikeMining')", false);

		// Load libraries we will user
		re.eval("library(xts)", false);
		re.eval("library(gbm)", false);
		
		// load data
		re.eval("train <- read.csv('input_regression_2.csv', stringsAsFactors=FALSE)", false);
		re.eval("d = dim(train)");
		System.out.println(x=re.eval("d"));
		
		// Do the pre-processing
		re.eval("train$workingday <- factor(train$workingday, c(0,1), ordered=FALSE)", false);
		re.eval("train$weather <- factor(train$weather, c(4,3,2,1), ordered=TRUE)", false);

		// set date time ####
		re.eval("train$datetime <- as.POSIXct(strptime(train$datetime, '%Y-%m-%d %H:%M:%S'))", false);		

		// create the linear model
		re.eval("genModel<-gbm(train$count~., data=train[,-c(1,7)], var.monotone=NULL, distribution='gaussian', n.trees=1200, shrinkage=0.05, interaction.depth=3, bag.fraction = 0.5, train.fraction = 1, n.minobsinnode = 10, cv.folds = 10, keep.data=TRUE, verbose=FALSE)");
		
		// choose the best iteration
		re.eval("best.iter <- gbm.perf(genModel,method='cv', plot.it=FALSE)");
		
//		System.out.println("best iter= " + re.eval("best.iter"));
		
		// predict using 500 samples
		re.eval("n <- 500");
		re.eval("i.test <- sample(1:nrow(train), n)");
		re.eval("test.1 = train[i.test, 1:6]");
		// create test target variable
		re.eval("test.1.target = train[i.test, 7]");
		//
		re.eval("test.1.pred <- predict(genModel, test.1[,-c(1)], best.iter, type='response')");
		//
		System.out.println(x = re.eval("summary(test.1.pred)"));
		
		System.out.print("rmsle = ");
		System.out.println(x=re.eval("(test.1.rmsle <- ((1/n)*sum(log(test.1.pred+1)-log(test.1.target+1))^2)^0.5)"));
		
	}
	
	public void runRScriptForArimaPrediction(Rengine re){
		REXP x;
		// Set the working directory
		re.eval("setwd('~/Documents/OU/DataMining/BikeMining')", false);
		re.eval("d = dim(train)");
	}
	
	public void runRScript( String[] args){
		String jriArgs[] = {"--no-save"};
		Rengine re = new Rengine(jriArgs, false, null);
		
		this.runRScriptForBoostedPrediction(re);
	}
}
