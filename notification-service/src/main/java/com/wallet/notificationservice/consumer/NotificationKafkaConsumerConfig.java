package com.wallet.notificationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

@EnableKafka
@Configuration
public class NotificationKafkaConsumerConfig {

    private static Logger logger = LoggerFactory.getLogger(NotificationKafkaConsumerConfig.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "USER_CREATED", groupId = "notification-email-service")
    public void sendWelcomeEmail(String message) throws JsonProcessingException {

        // TODO: handle object mapper's exception using try catch
        Map<String, Object> payload = objectMapper.readValue(message, Map.class);
        logger.info("Data from USER_CREATED topic : {}", payload);

        String email = (String) payload.get("email");
        String name = (String) payload.get("name");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("rahuld7219@gmail.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Welcome to WalletMe family!");
        simpleMailMessage.setText("Hi " + name + ", Your account is ready. Happy walletMeeeing :)");
//        simpleMailMessage.setCc("<--cc:emailIds-->");

        javaMailSender.send(simpleMailMessage);

        logger.info("Email sent to {}", email);
    }

    @KafkaListener(topics = "WALLET_UPDATED", groupId = "notification-email-service")
    public void sendWalletUpdateEmail(String message) throws JsonProcessingException {

        // TODO: handle object mapper's exception using try catch
        Map<String, Object> payload = objectMapper.readValue(message, Map.class);
        logger.info("Data from WALLET_UPDATED topic : {}", payload);

        String email = (String) payload.get("email");
        Double balance = (Double) payload.get("balance");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("rahuld7219@gmail.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Wallet Update!!");
        simpleMailMessage.setText("Hi, Your updated wallet balance is " + balance + "Happy walletMeeeing :)");
//        simpleMailMessage.setCc("<--cc:emailIds-->");

        javaMailSender.send(simpleMailMessage);

        logger.info("Email sent to {}", email);
    }
}
