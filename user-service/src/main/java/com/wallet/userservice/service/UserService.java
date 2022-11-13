package com.wallet.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.userservice.dao.UserRepo;
import com.wallet.userservice.entity.User;
import com.wallet.userservice.request.UserCreationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate; // key in string and data/msg in string

    @Autowired
    private ObjectMapper objectMapper;

    private String topic = "USER_CREATED"; // TODO: put this in properties file or in some class for constants

    /**
     * create user and returns created user id
     *
     * @param userCreationRequestDTO
     * @return
     */
    // TODO: make this transactional and handle problems like what if connection to Kafka broker not established,
    //  then don't allow creating entry in database
    public Long createUser(UserCreationRequestDTO userCreationRequestDTO) throws JsonProcessingException, ExecutionException, InterruptedException {
        User user =  User.builder().email(userCreationRequestDTO.getEmail())
                .name(userCreationRequestDTO.getName())
                .phone(userCreationRequestDTO.getPhone())
                .kycId(userCreationRequestDTO.getKycId())
                .build();

        userRepo.save(user); // TODO: handle when this gives error, if user already exist

        // In our system we have microservices all in java only, but there may be cases where some microservice may be
        // written in some other language (say, Python, Golang, etc), So it is best practice to store msg/data
        // in Kafka in Json(String) form, which can be easily consumed by any language.
        // So, for json format, we create key-value pair using map as below and then stringify it using object mapper while storing.
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getId()); // required by wallet-service
        payload.put("email", user.getEmail()); // required by notification-service
        payload.put("name", user.getName()); // required by notification-service

        ListenableFuture<SendResult<String, String>> kafkaResponseFuture = kafkaTemplate.send(topic,
                user.getId().toString(),
                objectMapper.writeValueAsString(payload)); // TODO: handle object mapper's exception using try catch

        logger.info("Pushed in {}, Kafka Response: {}", topic, kafkaResponseFuture.get()); // TODO: handle exception of future.get() using try-catch

        return user.getId(); // user refer to the user saved in database, so no need to store the returned user
    }
}
