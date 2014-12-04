--# CREATE Database
--CREATE DATABASE bikedata;

--# Run this script read csv data to a PostgresSQl database.

CREATE TABLE bike_station(
  station_id integer NOT NULL,
  name text NOT NULL,
  lat double precision NOT NULL,
  "long" double precision NOT NULL,
  dockcount integer NOT NULL,
  landmark text NOT NULL,
  installation date NOT NULL,
  clusterid integer, -- Cluster that this station belongs to based on location and bike activity. This is information that we got after KMeans.
  prediction double precision, -- bike prediction for the next hour
  lastmodified timestamp with time zone, -- The last time we made modfication to this table. Manily when we last update the prediction attribute
  CONSTRAINT bike_station_pkey PRIMARY KEY (station_id)
)


CREATE TABLE IF NOT EXISTS bike_trip(
	trip_id integer NOT NULL,
	duration integer NOT NULL,
	startDate timestamp NOT NULL,
	startStation text NOT NULL,
	startTerminal integer NOT NULL,
	endDate timestamp NOT NULL,
	endStation text NOT NULL,
	endTerminal integer NOT NULL,
	bikeNumber integer NOT NULL,
	subscription text NOT NULL,
	zipcode text,
	PRIMARY KEY(trip_id)
);


-- DROP TABLE bike_rebalancing;
CREATE TABLE IF NOT EXISTS bike_rebalancing(
 	station_id integer NOT NULL,
	bikes_available integer NOT NULL,
	docks_available integer NOT NULL,
	rebalancing_time timestamp NOT NULL
);


 CREATE TABLE IF NOT EXISTS bike_hourlystatus(
 	station_id integer NOT NULL,
	bikes_available integer NOT NULL,
	docks_available integer NOT NULL,
	rebalancing_time timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS weather(
	wDate date NOT NULL,
	Max_Temperature_F integer NOT NULL,
	Mean_Temperature_F integer NOT NULL,
	Min_TemperatureF integer NOT NULL,
	Max_Dew_Point_F	integer NOT NULL,
	MeanDew_Point_F	integer NOT NULL,
	Min_Dewpoint_F integer NOT NULL,
	Max_Humidity integer NOT NULL,
	Mean_Humidity integer NOT NULL,
	Min_Humidity integer NOT NULL,
	Max_Sea_Level_Pressure_In decimal NOT NULL,
	Mean_Sea_Level_Pressure_In decimal NOT NULL,
	Min_Sea_Level_Pressure_In decimal NOT NULL,
	Max_Visibility_Miles integer NOT NULL,
	Mean_Visibility_Miles integer NOT NULL,
	Min_Visibility_Miles integer NOT NULL,
	Max_Wind_Speed_MPH integer NOT NULL,
	Mean_Wind_Speed_MPH integer NOT NULL,
	Max_Gust_Speed_MPH integer,
	Precipitation_In text,
	Cloud_Cover integer,
	Events text,
	Wind_Dir_Degrees integer,
	zip integer NOT NULL
);

--# dump data into the table.
--# Make 
\copy bike_station FROM '~/Downloads/BikeMining/src/main/resources/data/201402_station_data.csv' DELIMITER ',' CSV HEADER; 
\copy bike_trip FROM '~/Downloads/BikeMining/src/main/resources/data/trip_data3.csv' DELIMITER ',' CSV HEADER; 
\copy bike_rebalancing FROM '~/Downloads/BikeMining/src/main/resources/data/201402_rebalancing_data.csv' DELIMITER ',' CSV HEADER;
\copy bike_hourlystatus FROM '~/Downloads/BikeMining/src/main/resources/data/201402_hourlystatus_data.csv' DELIMITER ',' CSV HEADER;
\copy weather FROM '~/Downloads/BikeMining/src/main/resources/data/201402_weather_data.csv' DELIMITER ',' CSV HEADER;
