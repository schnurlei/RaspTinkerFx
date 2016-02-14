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
import java.util.concurrent.ArrayBlockingQueue;
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
public class MqttPushWeatherData1 {

    private TinkerForgeConnection connection;
    private MqttWebsocktetBuffer buffer;

    public MqttPushWeatherData1(String ambientUid) throws InterruptedException {

    }

    public static void main(String[] args) {

        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(10);
        //Producer producer = new Producer(queue);
        TinkerforgeProducer producer = new TinkerforgeProducer(queue);

        Consumer consumer = new Consumer(queue);
        //qttConsumer consumer = new MqttConsumer(queue);
        new Thread(producer).start();
        new Thread(consumer).start();
        System.out.println("Producer and Consumer has been started");

    }

    public static class Producer implements Runnable {

        private BlockingQueue<Message> queue;

        public Producer(BlockingQueue<Message> q) {
            this.queue = q;
        }

        @Override
        public void run() {
            //produce messages
            for (int i = 0; i < 1000; i++) {
                Message msg = new Message("" + i);
                try {
                    Thread.sleep(500);
                    queue.put(msg);
                    System.out.println("Produced " + msg.getMsg());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //adding exit message
            Message msg = new Message("exit");
            try {
                queue.put(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static class Consumer implements Runnable {

        private final BlockingQueue<Message> queue;

        public Consumer(BlockingQueue<Message> q) {
            this.queue = q;
        }

        @Override
        public void run() {
            try {
                Message msg;
                //consuming messages until exit message is received
                while (!"exit".equals((msg = queue.take()).getMsg())) {
                    Thread.sleep(10);
                    System.out.println("Consumed " + msg.getMsg());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Message {

        private final String msg;

        public Message(String str) {
            this.msg = str;
        }

        public String getMsg() {
            return msg;
        }

    }
}
