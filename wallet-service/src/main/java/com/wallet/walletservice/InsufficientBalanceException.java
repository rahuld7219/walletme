package com.wallet.walletservice;

// We extends Exception as we not only want to throw this exception,
// but this must be catched also, as we don't want the program to stop when user don't have sufficient balance,
// and to force it to be catched at compile time we make it checked exception by extending Exception and not RuntimeException
public class InsufficientBalanceException extends Exception {

    public InsufficientBalanceException(String msg) {
        super(msg);
    }
}
