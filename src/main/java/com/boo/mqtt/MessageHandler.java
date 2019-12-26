package com.boo.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageHandler implements IMqttMessageListener {
    @Override
    public void messageArrived(String topic, MqttMessage message){
        log.info("message arrived, topic: " + topic + " message: " + message.toString());
        
        //
    }
}
