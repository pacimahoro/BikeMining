define(function(require, exports, module) {
  "use strict";

    // External dependencies.
    var Backbone = require("backbone");

    var app = require("app");
    var Layout = require("layoutmanager");
    var MapBox = require("mapbox");
    var MapModule = require("modules/map/index");

  // Defining the application router.
  var Router = Backbone.Router.extend({
      initialize: function(){

          var stationCollection = new MapModule.StationCollection();
          //timeout after 20 mins.
          stationCollection.fetch({timeout:12000000});

          var mainLayout = Layout.extend({
              el: "main",

              template: require("ldsh!./templates/main"),

              views:{
                  "#map": new MapModule.Views.MapView({collection: stationCollection})
              }
          });

          this.mainView = new mainLayout();
          this.mainView.render();
      },
      routes: {
            "": "index"
      },
      index: function() {
          console.log("Welcome to your / route.");
      }
  });

  module.exports = Router;
});
