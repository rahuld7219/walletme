package com.wallet.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.walletservice.dao.WalletRepo;
import com.wallet.walletservice.entity.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Map;

@EnableKafka
@Configuration
public class WalletKafkaConsumerConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepo walletRepo;

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
                                                                        // json numbers converted as Integer,
                                                                        // even though before serialization it was Long in our case.
                .email((String) payload.get("email"))
                .balance(100.00).build();

        walletRepo.save(wallet);
    }

    @KafkaListener(topics = "TRANSACTION_INIT", groupId = "wallet-service")
    public void listenTransactionInitTopic(String message) throws JsonProcessingException {
        logger.info("Consuming: {}", message);

        // TODO: handle object mapper's exception using try catch
        Map<String, Object> payload = objectMapper.readValue(message, Map.class);
    }
}
