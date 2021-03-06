package de.jdynameta.rasptinker.model.mqtt;

import io.inventit.dev.mqtt.paho.MqttWebSocketAsyncClient;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

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
/**
 *
 * @author rainer
 */
public class MqttWebsocketClientBuilder {
 
  private String uri;
  private String clientUID;
  private boolean memoryPersistence = true;
  private boolean filePersistence = false;
 
  public  IMqttAsyncClient build(){
    
    IMqttAsyncClient client;
    try {
      if(memoryPersistence){
        client = new MqttWebSocketAsyncClient(
        uri, clientUID, new MemoryPersistence());
      } else{
        client = new MqttWebSocketAsyncClient( uri,clientUID,
                    new MqttDefaultFilePersistence());
      }
      
    } catch (MqttException e) {
      e.printStackTrace();
      client = null;
    }
    return client;
  }
 
  public MqttWebsocketClientBuilder uri(String s) {
    this.uri = s;
    return this;
  }
 
  public MqttWebsocketClientBuilder clientUID(String s) {
    this.clientUID = s;
    return this;
  }
  public MqttWebsocketClientBuilder clientUIDGenerated() {
    String substring = UUID.randomUUID()
                            .toString().replace("-", "").substring(0, 22);
    this.clientUID = substring;
    System.out.println("clientUID = " + clientUID);
    return this;
  }
 
  public MqttWebsocketClientBuilder memoryPersistence(boolean b) {
    this.memoryPersistence = b;
    this.filePersistence = !this.memoryPersistence;
    return this;
  }
 
  public MqttWebsocketClientBuilder filePersistence(boolean b) {
    this.filePersistence = b;
    this.memoryPersistence = !this.filePersistence;
    return this;
  }
}