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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author rainer
 */
public class MttqWeatherReceiver
{
	public static final String BROKER = "192.168.178.71";  //broker
 
	public MttqWeatherReceiver() throws MqttException
	{

		MqttClient empf = new MqttClient(
				"tcp://"+BROKER+":1883", "MyfirstMQTTEmpf",
				new MemoryPersistence());
		empf.setCallback(new MqttCallback()
		{
			@Override
			public void connectionLost(Throwable throwable)
			{
			}

			@Override
			public void messageArrived(String str, MqttMessage mqttMessage)
					throws Exception
			{
				byte[] payload = mqttMessage.getPayload();
				String lastMessage = new String(payload);
				System.out.println("s = " + str + " msg "
						+ lastMessage);
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
			{
			}
		});
		empf.connect();
		empf.subscribe(MqttPushWeatherData.TOPIC, 1);
	}
	
	public static void main(String[] args)
	{
		try
		{
			new MttqWeatherReceiver();
		} catch (MqttException ex)
		{
			Logger.getLogger(MqttPushWeatherData.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}
}
