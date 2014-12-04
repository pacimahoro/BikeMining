/**
 * Created by pacifique on 10/21/14.
 */
define(function(require, exports, module){
    "use strict";

    var Layout = require("layoutmanager");
    var App = require("app");
    var backbone = require("backbone");
    var DateFormatter = require("dateformatter");

    var view = Layout.extend({
//        template: require("ldsh!./templates/faq"),

        initialize: function() {
            App.markerArr = [];

            if(this.collection){
                this.listenTo(this.collection, "reset sync request", this.syncMap);
            }
        },

        cls: "map-zone",

        getDescriptionElementForStation: function(station){
            var predictionsEl= [];

            var preds = station.get("predictions");

            if(preds != null){
                predictionsEl.push("<span><b>"+ (preds > 0) ? preds.toFixed(2) : preds +"</b></span>");
            }

            predictionsEl = predictionsEl.join("");
            var date = new Date(station.get("time"));
            var lastPredictionDate = new Date(station.get("lastpredictiontime"));

             var el = "<div>" +
                 "<p>Available Bikes: <span class=\"value\">"+ station.get("availablebikes")+"</span><br />"+
                 "Available Docks: <span class=\"value\">"+ station.get("availabledocks")+"</span><br />"+
                 "Current Time: <span class=\"value\">"+ date.toString("h:mm:ss tt") +"</span></p>"+
                 "<p>Next Hour Prediction: "+ predictionsEl +"</p>"+
                 "<p><i>Last Prediction was: "+ lastPredictionDate +"</i></p>"+
                 "</div>";

            return el;
        },

        syncMap: function(){
            if(this.collection.length > 0){

                var clusterColorMap=["#ffa500", "#4099FF", "#00A707", "#E1E1E1"];

                var stationGeoJSON = [];

                // Add each station.
                this.collection.each(function(station){
                    var clusterColor = clusterColorMap[3];
                    if(station.get("clusterid") <= 3 && station.get("clusterid") >= 1){
                        clusterColor = clusterColorMap[station.get("clusterid") - 1];
                    }

                    stationGeoJSON.push({
                        // this feature is in the GeoJSON format: see geojson.org
                        // for the full specification
                        type: 'Feature',
                        geometry: {
                            type: 'Point',
                            // coordinates here are in longitude, latitude order because
                            // x, y is the standard for GeoJSON and many formats
                            coordinates: [
                                parseFloat(station.get("lon")),
                                parseFloat(station.get("lat"))
                            ]
                        },
                        properties: {
                            title: station.get("name"),
                            description: this.getDescriptionElementForStation(station),
//                            description: "Available bikes: "+ station.get("availablebikes")+"<br />"+
//                                "Available docks: "+ station.get("availabledocks")+"<br />"+"Predictions: " + station.get("predictions"),


//                            description: '1718 14th St NW, Washington, DC',
                            // one can customize markers by adding simplestyle properties
                            // https://www.mapbox.com/foundations/an-open-platform/#simplestyle
                            'marker-size': 'large',
                            'marker-color': clusterColor,
                            'marker-symbol': 'cafe'
                        }
                    });
                }, this);


                App.map.featureLayer.setGeoJSON(stationGeoJSON);

                // The HTML we put in bindPopup doesn't exist yet, so we can't just say
                // $('#mybutton'). Instead, we listen for click events on the map element which
                // will bubble up from the tooltip, once it's created and someone clicks on it.
                $('#map').on('click', '.leaflet-clickable', function() {
                    alert('Hello from Toronto!');
                });
            }
            else{
                console.log("could not load the station data");
            }
        },

        afterRender: function(){
            L.mapbox.accessToken = "pk.eyJ1IjoicGFjaW1haG9ybyIsImEiOiJfWVoyQ2xBIn0.qi2KQ3PiB0wSKqTLD5oUCw";
            App.map = L.mapbox.map('map', 'pacimahoro.kcgh04ke').setView([37.329732, -121.886943], 12);


//            App.map.addEventListener('click', function(e){
//                console.log("clicked on "+ e.latlng);
//            }, this);
        }
    });

    return view;
});