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

    @PostMapping
    public ResponseEntity<Long> doTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO) throws ExecutionException, JsonProcessingException, InterruptedException {
        Long transactionId = transactionService.doTransaction(transactionRequestDTO);
        return ResponseEntity.ok(transactionId);
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkStatus(@RequestParam Long transactionId) {
        return ResponseEntity.ok(transactionService.checkStatus(transactionId));
    }
}
