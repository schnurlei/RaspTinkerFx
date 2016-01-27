/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinker.server;


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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author rainer
 */
//@WebListener
public class MqttServletContextListener implements ServletContextListener {

    private static final Logger LOG =  Logger.getLogger(MqttServletContextListener.class.getName());
    private Server mqttBroker = new Server();

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            LOG.info("******* START MQTT BROKER *********");
            this.mqttBroker = createMqttServer();
            Thread.sleep(20000);
            LOG.info("Before self publish");
            this.mqttBroker.internalPublish(createWelcomeMessage());
            LOG.info("After self publish");
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        LOG.warning("Stopping broker");
        if (this.mqttBroker != null) {
            this.mqttBroker.stopServer();
        }
        LOG.warning("Broker stopped");
    }

    private Server createMqttServer() throws InterruptedException, IOException {

        Server newMqttBroker = new Server();
        final IConfig classPathConfig = new ClasspathConfig();
        List<? extends InterceptHandler> userHandlers = Arrays.asList(new PublisherListener());
        newMqttBroker.startServer(classPathConfig, userHandlers);
        return newMqttBroker;
    }

    private PublishMessage createWelcomeMessage() {
        
        PublishMessage message = new PublishMessage();
        message.setTopicName("/exit");
        message.setRetainFlag(true);
//        message.setQos(AbstractMessage.QOSType.MOST_ONE);
//        message.setQos(AbstractMessage.QOSType.LEAST_ONE);
        message.setQos(AbstractMessage.QOSType.EXACTLY_ONCE);
        message.setPayload(ByteBuffer.wrap("Hello World!!".getBytes()));
        
        return message;
   }
    
    static class PublisherListener extends AbstractInterceptHandler {

        @Override
        public void onPublish(InterceptPublishMessage msg) {
            LOG.info("Received on topic: " + msg.getTopicName() + " content: " + new String(msg.getPayload().array()));
        }
    }
}
