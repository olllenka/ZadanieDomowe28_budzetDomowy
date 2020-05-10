package com.example.demo.transaction;

import java.util.List;

public class TransactionUtil {
    public static double sumOfTransactionsAmount(List<Transaction> transactionList){
        double sum = 0;
        for (Transaction transaction : transactionList) {
            sum += transaction.getAmount();
        }
        return sum;
    }
}
