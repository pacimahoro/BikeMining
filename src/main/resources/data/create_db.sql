--# CREATE Database
--CREATE DATABASE bikedata;

--# Run this script read csv data to a PostgresSQl database.
--
--CREATE TABLE IF NOT EXISTS bike_station(
--	station_id integer NOT NULL,
--	name text NOT NULL,
--	lat double precision NOT NULL,
--	long double precision NOT NULL,
--	dockcount integer NOT NULL,
--	landmark text NOT NULL,
--	installation date NOT NULL,
--	PRIMARY KEY (station_id)
--);


DROP TABLE bike_trip;

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

-- CREATE TABLE IF NOT EXISTS bike_rebalancing(
--	station_id integer NOT NULL,
--	bikes_available integer NOT NULL,
--	docks_available integer NOT NULL,
--	rebalancing_time date NOT NULL
--);

--# dump data into the table.
--COPY bike_station FROM '/Users/Shared/data/201402_station_data.csv' DELIMITER ',' CSV HEADER; 
COPY bike_trip FROM '/Users/Shared/data/201402_trip_data.csv' DELIMITER ',' CSV HEADER; 
--COPY bike_rebalancing FROM '/Users/Shared/data/201402_rebalancing_data.csv' DELIMITER ',' CSV HEADER;
