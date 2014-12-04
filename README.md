# Predicting Bike Sharing Mining
# ==============================

This is an App for predicting bike availability at a given station of the Bay Area Bike Share System (http://www.bayareabikeshare.com/). 

Using the Bay Area BSS data collected between August 2013 and Feb, 2014, we implement bike prediction using a Boosted Regression available in R. We interface R through JRI, a Java Interface to R (which comes as part of rJava).

## Project Layout
### A database storing historical bikeshare and weather data
The data was downloaded from the Bay Area Website (http://www.bayareabikeshare.com/datachallenge) and was made available to the public during an App challenge conducted earlier this year.
It contains four main tables: bike_station table, rebalancing table (contains bike availability data per minute per station), weather table, and bike_rip table. In this project, we added an additional table called bike_hourlystatus which contains bike availability data per station per hour. It's build from the rebalancing table data. In addition, we added a few fields to the bike_station table

### Business Layer Package 
This layer contains a model that use this data to predict future number of bikes, we have implemented the boosted regression model, ARIMA and linear regression. However, we focus mainly on the boosted regression as our primary model.
The Coordinajor class serves as the controlling point for this layer

### Client Layer Package
This layer deals with communicating with the client app. Currently StationServlet is the one we use to handle client requests.

### Running the Application
There are two entry points for the server side:
- BikeMiningStarter class: this is the main class responsible for kickstarting the server and listen to incoming client requests.
- App class: this is the entry point to run and update all predictions. It will then save the prediction results in the database that way whenever a user requests data, the information will be there. We found more user-friendly than trying to run predictions on each request. Ideally, we would like to have this run as a background thread set on a timer. However, currently the user has to kickstart it manually.

### The Web App with an interactive map running locally 
Lastly, we give the user a simple web app (http://localhost:8080/). We use Backbone.js and Boostrap for building the app. We display the map using MapBox.js. To get the web app running you need to be running the BikeMiningStarter class located under the Client layer package. Below, we will explain how to do all the necessary setups.  

## Installation

To get the project running you will locally, you will need the following:

- PostreSQL DB (http://www.postgresql.org/download/)
- rJava Package which contains JRI (http://rforge.net/JRI/)
- Java IDE preferably Eclipse IDE.
- R (http://cran.r-project.org/bin/windows/base/)

## Setting up the Database
1. You will need a working PostgreSQL 9.x series install. Once you have that, run data/create_db.sql to create all the appropriate tables.

2. The datasets are available under the Datasets folders. Use the script located under src/main/resources/create_db.sql to create all the tables and dump the data. You will need to check and make sure the location of the table csv files is collect.

3. Once PostgreSQL is installed. Go to the datalayer package (com.cs5083.bikemining.datalayer). Open the DAOManager.java class and edit the PostgreSQL configuration (mainly its url, username and password).

## Building the whole application
Go to the application root level, right click and go to Run As > Run Configurations. Set the Goals to "clean install". And then run the Maven build. This step will install all the necessary jars that you need.

## Setting JRI properly using Eclipse
If you use Eclipse, you can follow the steps to set up JRI properly, otherwise you won't be able to run predictions.

1. Install rJava Package. It's easy to install from R Studio directly. Or Follow Instructions found here http://rforge.net/JRI/

2. Build the BikeMiningStarter.java as a Java Application. Then, right click inside the class and click on Run Configurations. This should open Run Configurations Manager. Click on the Arguments tab and set the VM arguments to:
-Djava.library.path=/Library/Frameworks/R.framework/Resources/library/rJava/jri/

    The above path corresponds to the location of the JRI package on your computer. If you installed JRI inside of R Studio, the path should be very similar.

3. Still under Run Configurations, switch to the Environment tab. Add a new environment variable like this:
    Set Variable to R_HOME
    Set Value to /Library/Frameworks/R.framework/Resources (this should be your R Home).
    Skip this step if R_HOME was already set.
4. Repeat Steps 3 and 4 with the App.java class located under the com.cs5083.bikemining. This is the other entry point and it's the process responsible for running predictions.

## Running the Application
1. Run the BikeMiningStarter class as a Java Application. It will start the TomCat server and wait on port 8080. So if you go to http://localhost:8080/, the web app will load the map and make a request to retrieve stations data. In less than 5 seconds, the stations will appear on the map as markers. 
2. Run the App class as a Java Application to kickstart a prediction run for all stations based on the current time.

