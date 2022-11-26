package com.wallet.transactionservice.request;

import lombok.Data;

@Data
public class TransactionRequestDTO {

    private Long fromUserId; // we assume that fromUserId (the logged-in user) is provided in the request, although this should be added/provided by the authorization microservice.
    private Long toUserId;
    private Double amount;
}
