package com.wallet.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.transactionservice.dao.TransactionRepo;
import com.wallet.transactionservice.entity.Transaction;
import com.wallet.transactionservice.enums.TransactionStatus;
import com.wallet.transactionservice.request.TransactionRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private String transactionInitTopic = "TRANSACTION_INIT"; // TODO: put this in properties file or in some class for constants

    /**
     * do the transaction and return the transaction id
     *
     * @param transactionRequestDTO
     * @return
     */
    // TODO: make this transactional and handle problems like what if connection to Kafka broker not established,
    //  then don't allow creating entry in database
    public Long doTransaction(TransactionRequestDTO transactionRequestDTO) throws JsonProcessingException, ExecutionException, InterruptedException {
        Transaction transaction = Transaction.builder()
                .fromUserId(transactionRequestDTO.getFromUserId())
                .toUserId(transactionRequestDTO.getToUserId())
                .amount((transactionRequestDTO.getAmount()))
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepo.save(transaction);

        // TODO: handle object mapper's exception using try catch
        String payload = objectMapper.writeValueAsString(transaction);

        ListenableFuture<SendResult<String, String>> kafkaResponseFuture = kafkaTemplate.send(transactionInitTopic, String.valueOf(transaction.getFromUserId()), payload);

        logger.info("Pushed to topic: {}, kafka response: {}", transactionInitTopic, kafkaResponseFuture.get());// TODO: handle exception of future.get() using try-catch

        return transaction.getId();
    }
}
