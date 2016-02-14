/*
 * Copyright 2016 rainer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.rasptinker.model.mqtt;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author rainer
 */
public class MqttConsumer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(MqttConsumer.class.getName());
    private static final String TOPIC = "TinkerForge/Wetterstation/";
    private static final String BROKER = "localhost";

    private final BlockingQueue<MqttPushWeatherData1.Message> queue;
    private boolean stop;
    private final IMqttAsyncClient clientSender;
    private final MqttConnectOptions options;

    public MqttConsumer(BlockingQueue<MqttPushWeatherData1.Message> queue) {

        this.queue = queue;
        this.stop = false;
        MqttWebsocketClientBuilder MQTT_BUILDER = new MqttWebsocketClientBuilder();
        this.clientSender = MQTT_BUILDER.uri("ws://" + BROKER + ":8081").clientUIDGenerated().build();
        this.options = new MqttConnectOptions();
        this.options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        this.options.setCleanSession(true);
        //        options.setUserName(userName);
        //        options.setPassword(password.toCharArray());
    }

    public void stopConsumer() {
        this.stop = true;
    }

    @Override
    public void run() {

        MqttWebsocktetBuffer buffer = new MqttWebsocktetBuffer()
                .client(clientSender).topic(TOPIC).qos(1).retained(true);

        while (!stop) {

            try {
                if (!clientSender.isConnected()) {
                    clientSender.connect(options);
                }
            } catch (MqttException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

            try {
                MqttPushWeatherData1.Message msg = queue.take();
                buffer.sendAsync(msg.getMsg());
                LOGGER.log(Level.INFO, "Consumed {0}", msg.getMsg());
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
    
       private IMqttActionListener createMqttListener() {

        return new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                System.out.println("MQTT onSuccess " + asyncActionToken);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                System.out.println("MQTT onFailure " + asyncActionToken);
                exception.printStackTrace();
            }

        };
    }

}
