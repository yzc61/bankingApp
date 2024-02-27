package com.yzc.bankingapp.controller;

import com.yzc.bankingapp.model.MoneyTransferReq;
import com.yzc.bankingapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> initiateMoneyTransfer (@RequestHeader("Authorization") String bearerToken, @RequestBody MoneyTransferReq moneyTransferReq){
       // Transfers money from one account to another.
       // Transfers can occur simultaneously.
        return transactionService.initiateMoneyTransfer(bearerToken, moneyTransferReq);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> viewTransactionHistory (@RequestHeader("Authorization") String bearerToken,@PathVariable("accountId") UUID accountId){
        // Retrieves the transaction history for a specified account. Access is restricted to
        //    the account owner.
        return transactionService.viewTransactionHistory(bearerToken,accountId);
    }

}
