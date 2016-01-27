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

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.proto.messages.AbstractMessage;
import io.moquette.proto.messages.PublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.ClasspathConfig;
import io.moquette.server.config.IConfig;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static java.util.Arrays.asList;

/**
 *
 * @author rainer
 */
public class MqttTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        
        final IConfig classPathConfig = new ClasspathConfig();

        final Server mqttBroker = new Server();
        List<? extends InterceptHandler> userHandlers = asList(new PublisherListener());
        mqttBroker.startServer(classPathConfig, userHandlers);

        System.out.println("Broker started press [CTRL+C] to stop");
        //Bind  a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Stopping broker");
                mqttBroker.stopServer();
                System.out.println("Broker stopped");
            }
        });

        Thread.sleep(20000);
        System.out.println("Before self publish");
        PublishMessage message = new PublishMessage();
        message.setTopicName("/exit");
        message.setRetainFlag(true);
//        message.setQos(AbstractMessage.QOSType.MOST_ONE);
//        message.setQos(AbstractMessage.QOSType.LEAST_ONE);
        message.setQos(AbstractMessage.QOSType.EXACTLY_ONCE);
        message.setPayload(ByteBuffer.allocate(0).wrap("Hello World!!".getBytes()));
        mqttBroker.internalPublish(message);
        System.out.println("After self publish");

    }
    
    
    
    static class PublisherListener extends AbstractInterceptHandler {

        @Override
        public void onPublish(InterceptPublishMessage msg) {
            System.out.println("Received on topic: " + msg.getTopicName() + " content: " + new String(msg.getPayload().array()));
        }
    }

}