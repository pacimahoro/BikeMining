setwd('~/Documents/OU/DataMining/BikeMining')
library(xts)
library(gbm)

#load the data
train <- read.csv('input_regression_2.csv', stringsAsFactors=FALSE)


# data pre-processing
train$workingday <- factor(train$workingday, c(0,1), ordered=FALSE)
train$weather <- factor(train$weather, c(4,3,2,1), ordered=TRUE)

# set the time ####
train$datetime <- as.POSIXct(strptime(train$datetime, '%Y-%m-%d %H:%M:%S'))

# create a linear model
train.lm <- lm(count ~ ., data=train)

# display the lm model
train.lm

# choose n randomly selected rows of the train data
n <- 500
i.test <- sample(1:nrow(train), n)

# for each row, copy everything but except the actual 'count'
test.1 = train[i.test, 1:6]

# set the actual count as target, we will use them to compare how well our prediction is.
test.1.target = train[i.test, 7]

# run the predict
test.1.pred <- predict(train.lm, newdata=test.1)

# prediction summary
summary(test.1.pred)

# Compute the prediction root mean squared logarithmic error:
# RMSLE: ((log(p+1)-log(a+1))^2)^0.5
rmsle = (test.1.rmsle <- ((1/n)*sum(log(test.1.pred+1)-log(test.1.target+1))^2)^0.5)