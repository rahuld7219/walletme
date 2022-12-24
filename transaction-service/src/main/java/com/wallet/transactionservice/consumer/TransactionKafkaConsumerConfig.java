package com.wallet.transactionservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.transactionservice.dao.TransactionRepo;
import com.wallet.transactionservice.entity.Transaction;
import com.wallet.transactionservice.enums.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Map;

@EnableKafka
@Configuration
public class TransactionKafkaConsumerConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepo transactionRepo;

    public static Logger logger = LoggerFactory.getLogger(TransactionKafkaConsumerConfig.class);

    @KafkaListener(topics = "TRANSACTION_COMPLETED", groupId = "transaction-service")
    public void listenTransactionCompletedTopic(String message) throws JsonProcessingException { // TODO: handle exception with try catch
        logger.info("Consuming: {}", message);

        // TODO: handle object mapper's exception using try catch
        Map<String, Object> payload = objectMapper.readValue(message, Map.class);

        Long transactionId = Long.valueOf((Integer)payload.get("transactionId"));
        String status = (String) payload.get("status");

        Transaction transaction = transactionRepo.findById(transactionId).get();
        transaction.setStatus(TransactionStatus.valueOf(status));

        transactionRepo.save(transaction);
    }
}
