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

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author rainer
 */
public class MqttWebsocktetBuffer {
 
  static final ExecutorService fixedThreadPool 
                                  = Executors.newFixedThreadPool(4);
 
  private String topic;
  private IMqttAsyncClient client;
  private int qos = 1;
  private boolean retained = true;
 
  public MqttWebsocktetBuffer topic(String s) {
    this.topic = s;
    return this;
  }
 
  public MqttWebsocktetBuffer client(IMqttAsyncClient c) {
    this.client = c;
    return this;
  }
 
  public MqttWebsocktetBuffer qos(int q) {
    this.qos = q;
    return this;
  }
  public MqttWebsocktetBuffer retained(boolean b) {
    this.retained = b;
    return this;
  }
 
  public void sendAsync(String msg) {
    Supplier<String> task = () -> {
      try {
        client.publish(topic, (msg).getBytes("UTF-8"), 
                          qos, retained);
      } catch (MqttException
          | UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      return "Done - " + msg;
    };
    CompletableFuture.supplyAsync(task, fixedThreadPool)
        .thenAccept(System.out::println);
  }
}