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

genmod<-gbm(train$count~.
              ,data=train[,-c(1,7)] ## registered,casual,count columns
              ,var.monotone=NULL # which vars go up or down with target
              ,distribution="gaussian"
              ,n.trees=1200
              ,shrinkage=0.05
              ,interaction.depth=3
              ,bag.fraction = 0.5
              ,train.fraction = 1
              ,n.minobsinnode = 10
              ,cv.folds = 10
              ,keep.data=TRUE
              ,verbose=FALSE)

genmod<-gbm(train$count~., data=train[,-c(1,7)], var.monotone=NULL, distribution='gaussian', n.trees=1200, shrinkage=0.05, interaction.depth=3, bag.fraction = 0.5, train.fraction = 1, n.minobsinnode = 10, cv.folds = 10, keep.data=TRUE, verbose=FALSE)

# Find the best iter
best.iter <- gbm.perf(genmod,method="cv")

# choose n randomly selected rows of the train data
n <- 500
i.test <- sample(1:nrow(train), n)

# for each row, copy everything but except the actual 'count'
test.1 = train[i.test, 1:6]

# set the actual count as target, we will use them to compare how well our prediction is.
test.1.target = train[i.test, 7]

# run the predict
#test.1.pred <- predict(train.lm, newdata=test.1)
test.1.pred <- predict(genmod, test.1[,-c(1)], best.iter, type='response')

# prediction summary
summary(test.1.pred)

# Compute the prediction root mean squared logarithmic error:
# RMSLE: ((log(p+1)-log(a+1))^2)^0.5
rmsle = (test.1.rmsle <- ((1/n)*sum(log(test.1.pred+1)-log(test.1.target+1))^2)^0.5)
rmsle