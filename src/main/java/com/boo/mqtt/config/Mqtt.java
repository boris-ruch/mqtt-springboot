package com.boo.mqtt.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class Mqtt {

    @Value("${mqtt.publisher-id}")
    private String mqttPublisherId = "spring-server";

    @Value("${mqtt.server-address}")
    private String mqttServerAddres = "tcp://127.0.0.1:1883";
    
    @Bean
    public IMqttClient getInstance() throws MqttException {

        IMqttClient instance = new MqttClient(mqttServerAddres, mqttPublisherId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        if (!instance.isConnected()) {
            instance.connect(options);
        }
        return instance;
    }
}