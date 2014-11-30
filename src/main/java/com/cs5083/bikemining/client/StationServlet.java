package com.cs5083.bikemining.client;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cs5083.bikemining.businesslayer.Coordinator;
import com.cs5083.bikemining.datalayer.Station;
import com.cs5083.bikemining.datalayer.StationStatus;

@WebServlet(
	    name = "StationServlet", 
	    urlPatterns = {"/stations", "/predict", "/bikecount"}
	)
public class StationServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StationServlet() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if ("/stations".equalsIgnoreCase(req.getServletPath())) {
			List<Station> stations = Coordinator.getInstance().getStations();
			
			try{
				JSONObject respData = new JSONObject();
				JSONArray stationsData = new JSONArray();
				
				JSONObject obj = null, statusObj=null;
				for (Station station : stations) {
					obj = new JSONObject();
					
					obj.put("stationid", station.getId());
					obj.put("lat", station.getLatitude());
					obj.put("lon", station.getLongitude());
					obj.put("name", station.getName());
					
					StationStatus status = Coordinator.getInstance().getCurrentBikeStatus(station.getId());
					statusObj = new JSONObject();
					if(status != null){
						statusObj.put("time", status.getTime());
						statusObj.put("avalaiblebikes", status.getAvailableBikes());
						statusObj.put("avalaibledocks", status.getAvailableDocks());	
					}
					
					// set status
					obj.put("status", statusObj);
					stationsData.put(obj);
				}
				
				respData.put("data", stationsData);
				resp.getWriter().println(respData);
				resp.flushBuffer();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {

		}
	}
}
