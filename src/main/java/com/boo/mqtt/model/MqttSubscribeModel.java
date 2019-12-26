package com.boo.mqtt.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MqttSubscribeModel {

    private String message;
    private Integer qos;
    private Integer id;
    
}