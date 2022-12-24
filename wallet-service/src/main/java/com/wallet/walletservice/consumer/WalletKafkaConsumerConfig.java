package com.wallet.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.walletservice.InsufficientBalanceException;
import com.wallet.walletservice.dao.WalletRepo;
import com.wallet.walletservice.entity.Wallet;
import com.wallet.walletservice.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@EnableKafka
@Configuration
public class WalletKafkaConsumerConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private WalletService walletService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private String transactionTopic = "TRANSACTION_COMPLETED";

    public static Logger logger = LoggerFactory.getLogger(WalletKafkaConsumerConfig.class);

    @KafkaListener(topics = "USER_CREATED", groupId = "wallet-service") // we are giving groupId as we don't want this
                                                                        // message to be consumed by 2 wallet-service as we will
                                                                        // have multiple instances of wallet-service running
    public void listenUserCreatedTopic(String message) throws JsonProcessingException { // as we have pushed the message in json string form in kafka,
                                                                                    // so we can take it as String
        logger.info("Consuming: {}", message);

        // TODO: handle object mapper's exception using try catch
        Map<String, Object> payload = objectMapper.readValue(message, Map.class); // as message stored in json form,
                                                                                    // so we can read it as Map
        /**
         * Create initial wallet with Rs 100, after user creation
         * TODO: extract below code
         */
        Wallet wallet = Wallet.builder()
                .userId(Long.valueOf((Integer) payload.get("userId"))) // while deserializing json to object,
                                                                        // json non-floating numbers deserialized as Integer,
                                                                        // even though before serialization it was Long in our case.
                .email((String) payload.get("email"))
                .balance(100.00).build();

        walletRepo.save(wallet);
    }

    @KafkaListener(topics = "TRANSACTION_INIT", groupId = "wallet-service")
    public void listenTransactionInitTopic(String message) throws JsonProcessingException, ExecutionException, InterruptedException { // TODO: handle exception with try catch
        logger.info("Consuming: {}", message);

        // TODO: handle object mapper's exception using try catch
        Map<String, Object> payload = objectMapper.readValue(message, Map.class);

        Long senderId = Long.valueOf(String.valueOf(payload.get("fromUserId"))); // could typecast to Integer also deserialized to Integer
        Long receiverId = Long.valueOf((Integer)payload.get("toUserId"));
        Double amount = (Double) payload.get("amount");

        Map<String, Object> transactionPayload = new HashMap<>();
        transactionPayload.put("transactionId", payload.get("id"));

        try {
            walletService.updateWalletForTransaction(senderId, receiverId, amount); // doing transaction
            transactionPayload.put("status", "SUCCESSFUL");
        } catch (InsufficientBalanceException | ExecutionException | InterruptedException e) {
            logger.error("Exception while updating wallet {}", e);
            transactionPayload.put("status", "FAILED");
        }

        // TODO: extract below code and put in some general producer class
        ListenableFuture<SendResult<String, String>>  kafkaResponseFuture=  kafkaTemplate.send(transactionTopic, String.valueOf(senderId), objectMapper.writeValueAsString(transactionPayload));
        logger.info("Pushed to topic: {}, kafka response: {}", transactionTopic, kafkaResponseFuture.get());// TODO: handle exception of future.get() using try-catch
    }
}
