package com.wallet.notificationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Map;

@EnableKafka
@Configuration
public class NotificationKafkaConsumerConfig {

    private static Logger logger = LoggerFactory.getLogger(NotificationKafkaConsumerConfig.class);

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "USER_CREATED", groupId = "notification-email-service")
    public void sendWelcomeEmail(String message) throws JsonProcessingException {

        // TODO: handle object mapper's exception using try catch
        Map<String, Object> payload = objectMapper.readValue(message, Map.class);
        logger.info("Data from USER_CREATED topic : {}", payload);
    }
}
