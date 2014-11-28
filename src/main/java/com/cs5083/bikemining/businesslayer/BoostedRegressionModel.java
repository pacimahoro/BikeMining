package com.cs5083.bikemining.businesslayer;

import org.joda.time.DateTime;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class BoostedRegressionModel extends RegressionModel {

	public BoostedRegressionModel(int stationId) {
		super(stationId);
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
		re.eval("genModel<-gbm(train$count~., data=train[,-c(1,7)], var.monotone=NULL, distribution='gaussian', n.trees=1200, shrinkage=0.05, interaction.depth=3, bag.fraction = 0.5, train.fraction = 1, n.minobsinnode = 10, cv.folds = 10, keep.data=TRUE, verbose=FALSE)");
		
		// choose the best iteration
		re.eval("best.iter <- gbm.perf(genModel,method='cv', plot.it=FALSE)");
		
//				System.out.println("best iter= " + re.eval("best.iter"));
		
		// predict using 500 samples
		re.eval("n <- 500");
		re.eval("i.test <- sample(1:nrow(train), n)");
		re.eval("test.1 = train[i.test, 1:6]");
		// create test target variable
		re.eval("test.1.target = train[i.test, 7]");
		//
		re.eval("test.1['pred'] <- predict(genModel, test.1[,-c(1)], best.iter, type='response')");
		//
		System.out.println(x = re.eval("summary(test.1$pred)"));
		
		//Print prediction results:
		x = re.eval("test.1$pred");
		System.out.println(x);
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

}
