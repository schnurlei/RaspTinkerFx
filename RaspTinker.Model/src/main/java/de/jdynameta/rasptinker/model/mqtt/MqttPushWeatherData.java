/*
 * Copyright 2014 rainer.
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author rainer
 */
public class MqttPushWeatherData
{
	public static final String TOPIC = "TinkerForge/Wetterstation/";
	public static final String BROKER = "localhost";  //broker
    private static MqttClientBuilder builder = new MqttClientBuilder();
 	
	
	private final TinkerForgeConnection connection;
	private final MqttBuffer buffer;
	
	public MqttPushWeatherData( String ambientUid) throws MqttException
	{
		IMqttClient sender = builder.uri("tcp://" + BROKER+":1883").clientUIDGenerated().build();
		sender.connect();
		this.buffer = new MqttBuffer() //Implementierung in Listing 6
          .client(sender).topic(TOPIC).qos(1).retained(true);		
		this.connection = new TinkerForgeConnection();
		this.addAmbientLight(ambientUid);
		this.connect("localhost", 4223);
	}
	
	private void connect(String host, int port) {
		connection.connect(host, port);
	}

	private void addAmbientLight(String uid) {
		
		          BrickletAmbientLightV2 ambient =  this.connection.addAmbientLight(uid, 1000);
        ambient.addIlluminanceListener(illuminance -> {
			
			final String text = LocalDateTime.now() + " - Temp  : "
                            + illuminance + " °C";			
			buffer.sendAsync(text);
		});	
        
	}

	public static void main(String[] args)
	{
		try
		{
			new MqttPushWeatherData("uNU");
		} catch (MqttException ex)
		{
			Logger.getLogger(MqttPushWeatherData.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}
}
