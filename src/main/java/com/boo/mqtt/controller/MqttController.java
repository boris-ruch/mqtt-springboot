package com.boo.mqtt.controller;

import com.boo.mqtt.MessageHandler;
import com.boo.mqtt.model.MqttPublishModel;
import com.boo.mqtt.model.MqttSubscribeModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/mqtt")
@Slf4j
public class MqttController {

    private final IMqttClient mqttClient;

    private final MessageHandler messageHandler;

    @Value("${mqtt.topic}")
    private String topic;

    @Autowired
    public MqttController(IMqttClient mqttClient, MessageHandler messageHandler) {
        this.mqttClient = mqttClient;
        this.messageHandler = messageHandler;
    }

    @PostConstruct
    public void subscribe() throws MqttException {
        log.info("Subscribe to topic {}", topic);
        mqttClient.subscribe(topic, messageHandler);
        //just for testing
        this.publishMessage();
    }

    @PostMapping("publish")
    public void publishMessage(@RequestBody @Valid MqttPublishModel messagePublishModel,
                               BindingResult bindingResult) throws org.eclipse.paho.client.mqttv3.MqttException {
        if (bindingResult.hasErrors()) {
            throw new MqttException(1);
        }

        MqttMessage mqttMessage = new MqttMessage(messagePublishModel.getMessage().getBytes());
        mqttMessage.setQos(messagePublishModel.getQos());
        mqttMessage.setRetained(messagePublishModel.getRetained());
        mqttClient.publish(messagePublishModel.getTopic(), mqttMessage);
    }

    @GetMapping("publish/simple")
    public void publishMessage() throws org.eclipse.paho.client.mqttv3.MqttException {

        StringBuilder payload = new StringBuilder();
        payload.append("gateway-id:")
                .append(topic)
                .append("Hello Boo");

        MqttPublishModel model = MqttPublishModel.builder()
                .message(payload.toString())
                .qos(1)
                .retained(false)
                .topic(topic)
                .build();

        MqttMessage mqttMessage = new MqttMessage(model.getMessage().getBytes());
        mqttMessage.setQos(model.getQos());
        mqttMessage.setRetained(model.getRetained());

        mqttClient.publish(model.getTopic(), mqttMessage);
    }

    /**
     * example
     * http://localhost:8080/mqtt/subscribe?topic=foo&wait_millis=1000
     */
    @GetMapping("subscribe")
    public List<MqttSubscribeModel> subscribeChannel(@RequestParam(value = "wait_millis") Integer waitMillis)
            throws InterruptedException, org.eclipse.paho.client.mqttv3.MqttException {
        List<MqttSubscribeModel> messages = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        mqttClient.subscribeWithResponse(topic, (s, mqttMessage) -> {
            MqttSubscribeModel mqttSubscribeModel = MqttSubscribeModel.builder()
                    .id(mqttMessage.getId())
                    .message(new String(mqttMessage.getPayload()))
                    .qos(mqttMessage.getQos())
                    .build();
            messages.add(mqttSubscribeModel);
            countDownLatch.countDown();
        });

        countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);

        return messages;
    }


}