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

import com.tinkerforge.BrickletAmbientLightV2;
import de.jdynameta.rasptinker.model.TinkerForgeConnection;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rainer
 */
public class TinkerforgeProducer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(TinkerforgeProducer.class.getName());
    private final BlockingQueue<MqttPushWeatherData1.Message> queue;
    private boolean stop;
    private final TinkerForgeConnection connection;

    public TinkerforgeProducer(BlockingQueue<MqttPushWeatherData1.Message> queue) {

        this.queue = queue;
        this.stop = false;
        this.connection = new TinkerForgeConnection();
        this.addAmbientLight("uNU");
        this.connection.connect("localhost", 4223);
    }

    public void stopProducer() {
        this.stop = true;
    }

   private void addAmbientLight(String uid) {

        BrickletAmbientLightV2 ambient = this.connection.addAmbientLight(uid, 1000);
        ambient.addIlluminanceListener(illuminance -> {

            final String text = LocalDateTime.now() + " - Temp  : "
                    + illuminance + " °C";
            try {
                this.queue.put(new MqttPushWeatherData1.Message(text));
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        });

    }    
    
    
    @Override
    public void run() {
        

        while (!stop) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

    }
}
