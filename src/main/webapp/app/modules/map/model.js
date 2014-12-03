define(function(require, exports, module) {
    "use strict";

    var app = require("app");

    var Backbone = require("backbone");

    /* Model a station object. */
    var Station = Backbone.Model.extend({

        idAttribute: "stationid",

        defaults: {
            'lon':-121.886943,

            'stationid': 2,

            'name': "San Jose Diridon Caltrain Station",

            'lat':37.329732,

            'time': new Date(),

            'availabledocks': 8,

            'availablebikes': 19,

            'predictions':[],

            'clusterid': 1
        }
    });

    module.exports = Station;
});