package com.cs5083.bikemining.businesslayer;

import org.joda.time.DateTime;
import org.rosuda.JRI.Rengine;

import com.cs5083.bikemining.datalayer.Station;

public interface Predictor {
	
	/**
	 * Build model retrieves data from DB relating to a station
	 * then, pre-process the data and write the data to an input file
	 * @param station
	 * @return String - the name of the training input file
	 */
	public String buildTrainingModel(Station station);
	
	/**
	 * Predict the number of bikes given inputs.
	 * We predict starting from the currentTime and the following hourly bike prediction
	 * i.e. if we are 5pm, and the predictionNumber is 4, 
	 * our results will include prediction for 6pm, 7pm, 8pm and 9pm.
	 * @param rengine
	 * @param predictionNumber 
	 * @param targetTime
	 * @return PredictionItem - contains prediction results
	 */
	public PredictionItem predict(Rengine rengine, int predictionNumber, DateTime currentTime);
	
	/**
	 * Predict the number of bikes given but for testing using randomly selected values from the training set.
	 * this should ONLY be used to test the performance of the algorithm.
	 * @param re
	 * @param predictionNumber
	 * @param currentTime
	 * @return PredictionItem - contains prediction results
	 */
	public PredictionItem predictWithRandomData(Rengine re, int predictionNumber,
			DateTime currentTime); 
	
	/**
	 * Build test model for this prediction
	 * @param station
	 * @param predictionNumber
	 * @param startingTime
	 * @return String - the name of the test input file
	 */
	public String buildTestModel(Station station, int predictionNumber, DateTime startingTime);

}
