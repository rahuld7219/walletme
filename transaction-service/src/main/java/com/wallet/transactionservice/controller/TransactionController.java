package com.wallet.transactionservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wallet.transactionservice.dao.TransactionRepo;
import com.wallet.transactionservice.request.TransactionRequestDTO;
import com.wallet.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // We have returned the UUID for the unique transaction id which can not be easily guessed as
    // publicly exposed API should not use number that could be easily guessed, so we have not returned the id as then
    // anyone can take that number and can see other transactions also simply by just incrementing/decrementing the number.
    @PostMapping
    public ResponseEntity<String> doTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO) throws ExecutionException, JsonProcessingException, InterruptedException {
        String transactionId = transactionService.doTransaction(transactionRequestDTO);
        return ResponseEntity.ok(transactionId);
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkStatus(@RequestParam String transactionId) {
        return ResponseEntity.ok(transactionService.checkStatus(transactionId));
    }
}
