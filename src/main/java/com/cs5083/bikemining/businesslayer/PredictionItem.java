package com.cs5083.bikemining.businesslayer;

import com.cs5083.bikemining.datalayer.StationStatus;

public class PredictionItem {
	
	private int stationId;
	private StationStatus currentStatus;
	private int predictionNumber;
	private double[] bikePredictionResults;
	
	private double rmsle;
	
	public PredictionItem(double[] results, int predictionNumber, double predictionError) {
		this.predictionNumber = predictionNumber;
		this.rmsle = predictionError;
		this.bikePredictionResults = results;
	}
	
	public void setCurrentStatus(StationStatus status){
		this.currentStatus = status;
	}
	
	public StationStatus getCurrentStatus(){
		return this.currentStatus;
	}
	
	public double[] getResults(){
		return this.bikePredictionResults;
	}
	
	public int count(){
		return predictionNumber;
	}
	
	public double getPredictionError(){
		return this.rmsle;
	}
}
