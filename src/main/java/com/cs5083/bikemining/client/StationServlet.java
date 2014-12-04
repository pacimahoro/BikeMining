package com.cs5083.bikemining.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cs5083.bikemining.businesslayer.Coordinator;
import com.cs5083.bikemining.businesslayer.PredictionItem;
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
	
	protected String decodeAjaxRequest(HttpServletRequest req) throws IOException {
	    InputStream inputStream = req.getInputStream();
        ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
        byte[] requestBuffer = new byte[65536];
        int count = 0;
        while( count >= 0 ) {
            count = inputStream.read(requestBuffer);
            if(count >= 0 ) byteArrOutStream.write(requestBuffer, 0, count);
        }
        String encodedData = new String(byteArrOutStream.toByteArray(), "UTF-8");
        return URLDecoder.decode(encodedData, "UTF-8");
	}
	
	protected Map<String, String> mapRequestData(String requestData) throws UnsupportedEncodingException {
        String[] requestDataParts = requestData.split("&");
        Map<String, String> mappedData = new HashMap<String, String>();
        for( String currentRequestData : requestDataParts ) {
            String[] dataParts = currentRequestData.split("=");
            if(dataParts.length == 2) {
            	mappedData.put(URLDecoder.decode(dataParts[0], "UTF-8"), URLDecoder.decode(dataParts[1], "UTF-8"));
            }
        }
        return mappedData;
    }
	
	private JSONObject getStatusObject(StationStatus status) throws JSONException{
		JSONObject statusObj = new JSONObject();
		if(status != null){
			statusObj.put("time", status.getTime());
			statusObj.put("avalaiblebikes", status.getAvailableBikes());
			statusObj.put("avalaibledocks", status.getAvailableDocks());	
		}
		return statusObj;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if ("/stations".equalsIgnoreCase(req.getServletPath())) {
			List<Station> stations = Coordinator.getInstance().getStations();
			try{
				JSONObject respData = new JSONObject();
				JSONArray stationsData = new JSONArray();
				
				JSONObject obj = null;
				Station station = null;
				PredictionItem prediction = null;
				for (int i = 0; i < stations.size(); i++) {
					station = stations.get(i);
					 
					obj = new JSONObject();
						
					obj.put("stationid", station.getId());
					obj.put("lat", station.getLatitude());
					obj.put("lon", station.getLongitude());
					obj.put("name", station.getName());
					obj.put("clusterid", station.getClusterId());
					
					StationStatus status = Coordinator.getInstance().getCurrentBikeStatus(station.getId());
					if(status != null){
						LocalDateTime lt = new LocalDateTime(status.getTime().getMillis());
						obj.put("time", lt);
						obj.put("availablebikes", status.getAvailableBikes());
						obj.put("availabledocks", status.getAvailableDocks());	
					}
					
					obj.put("predictions", station.getLastPrediction());
					LocalDateTime dt = new LocalDateTime(station.getLastModified().getMillis());
					obj.put("lastpredictiontime", dt);
					stationsData.put(obj);
				}
				
//				respData.put("data", stationsData);
				resp.getWriter().println(stationsData);
				resp.flushBuffer();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 
		else if ("/predict".equalsIgnoreCase(req.getServletPath())) {
			System.out.println("Start processing prediction request...");
			
			System.out.println(decodeAjaxRequest(req));
			Map<String, String> translatedReqData = mapRequestData(decodeAjaxRequest(req));
			
			try{
				String m = req.getParameter("stationid");
				int stationId = Integer.parseInt(m);
				
				DateTime currentTime = DateTime.now().minusMonths(12);
				// Request the projected number of bikes for this station
				Coordinator coordinator = Coordinator.getInstance();
				PredictionItem predictionItem = coordinator.runPrediction(stationId, currentTime, 5);
				
				JSONObject respData = new JSONObject();
				JSONObject obj = new JSONObject();
				obj.put("class", "PredictionObject");
				
				if(predictionItem != null){
					obj.put("stationid", stationId);
					
					StationStatus status = predictionItem.getCurrentStatus();
					if(status != null){
						obj.put("time", status.getTime());
						obj.put("availablebikes", status.getAvailableBikes());
						obj.put("availabledocks", status.getAvailableDocks());	
					}
					
					JSONArray resultsObj = new JSONArray();
					
					for (double res : predictionItem.getResults()) {
						resultsObj.put(res);
					}
					obj.put("results", resultsObj);
				}
				
				respData.put("data", obj);
				resp.getWriter().println(respData);
				resp.flushBuffer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
