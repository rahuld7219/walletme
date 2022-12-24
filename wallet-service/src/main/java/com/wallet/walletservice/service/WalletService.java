package com.wallet.walletservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.walletservice.InsufficientBalanceException;
import com.wallet.walletservice.dao.WalletRepo;
import com.wallet.walletservice.entity.Wallet;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class WalletService {

    private static Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WalletRepo walletRepo;

    private String topic = "WALLET_UPDATED";

    @Transactional // it will hold lock on the wallets(database table) used in this method, so no other can update these wallets in the meantime.
    public void updateWalletForTransaction(Long senderId, Long receiverId, Double amount) throws ExecutionException, JsonProcessingException, InterruptedException, InsufficientBalanceException {
        Wallet senderWallet = walletRepo.findByUserId(senderId);
        Wallet receiverWallet = walletRepo.findByUserId(receiverId);

        if (senderWallet.getBalance() >= amount) { // TODO: can write this validation in different file/method
            senderWallet.setBalance(senderWallet.getBalance() - amount);
            receiverWallet.setBalance(receiverWallet.getBalance() + amount);

            /* the below way hit/call the database 2 times */
//            walletRepo.save(senderWallet);
//            walletRepo.save(receiverWallet);

            List<Wallet> wallets = new ArrayList<>();
            wallets.add(senderWallet);
            wallets.add(receiverWallet);

            /* this way hit the database only once to save all the wallets */
            walletRepo.saveAll(wallets);

            pushWalletUpdateEvent(senderWallet); // TODO: send the amount deducted also in the topic for the notification
            pushWalletUpdateEvent(receiverWallet); // TODO: send the amount received also in th topic for the notification

            return;
        }
        throw new InsufficientBalanceException("Your wallet balance is not enough to do the transaction.");
    }

    public void pushWalletUpdateEvent(Wallet wallet) throws JsonProcessingException, ExecutionException, InterruptedException { // TODO: handle exception using try catch and custom execptions
        String payload = objectMapper.writeValueAsString(wallet); // TODO: can store only the required data, not the entire row

        ListenableFuture<SendResult<String, String>> kafkaResponseFuture = kafkaTemplate.send(topic, String.valueOf(wallet.getUserId()), payload);

        logger.info("Pushed to topic: {}, kafka response: {}", topic, kafkaResponseFuture.get());// TODO: handle exception of future.get() using try-catch
    }
}
