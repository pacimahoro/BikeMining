define(function(require, exports, module) {
    "use strict";

    module.exports = {

        model: require("./model"),

        StationCollection: Backbone.Collection.extend({
            model: require("./model"),
            url: '/stations'
        }),

        Views: {
            MapView: require("./view")
        }
    };
});