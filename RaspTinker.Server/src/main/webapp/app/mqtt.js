angular.module('mqtt', [])
        .constant('MAIN_NAVIGATION_ITEMS', {
            kultur: 'Kultur',
            reisen: 'Reisen',
            service: 'Service'
        }).run(function () {
            
    var client;
    var location = {
        hostname: 'localhost',
        port: '8081'
    };


    // called when a message arrives
    function onMessageArrived(message) {
        debugMessage(message);
        gauge.setValueAnimated(Number(message.payloadString));
    }

    // called when the client loses its connection
    function onConnectionLost(responseObject) {
        if (responseObject.errorCode !== 0) {
            console.log("onConnectionLost:" + responseObject.errorMessage);
        }
    }


    // called when the client connects
    function onConnect() {
        // Once a connection has been made, make a subscription and send a message.
        console.log("onConnect");
        client.subscribe("/test");
        var message = new Paho.MQTT.Message("11");
        message.destinationName = "/test";
        client.send(message);
    }


    client = new Paho.MQTT.Client(location.hostname, Number(location.port), "clientId");

    // set callback handlers
    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;

    // connect the client
    client.connect({onSuccess: onConnect});

})
        .provider("Navigation", ['MAIN_NAVIGATION_ITEMS', function (MAIN_NAVIGATION_ITEMS) {




                this.$get = function () {
                    return {
                        getPages: function () {
                            return pages;
                        }
                    };
                };
            }]);



