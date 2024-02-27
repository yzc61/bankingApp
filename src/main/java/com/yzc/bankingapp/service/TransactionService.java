package com.yzc.bankingapp.service;

import com.yzc.bankingapp.ResponseModelUtil;
import com.yzc.bankingapp.model.*;
import com.yzc.bankingapp.repository.AccountModelRepository;
import com.yzc.bankingapp.repository.TransactionModelRepository;
import com.yzc.bankingapp.repository.UserModelRepository;
import com.yzc.bankingapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.yzc.bankingapp.model.TransactionStatus.FAILED;
import static com.yzc.bankingapp.model.TransactionStatus.SUCCESS;

@Service
public class TransactionService {

    @Autowired
    ResponseModelUtil responseModelUtil;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserModelRepository userModelRepository;

    @Autowired
    TransactionModelRepository transactionModelRepository;

    @Autowired
    AccountModelRepository accountModelRepository;



    public ResponseEntity<?> initiateMoneyTransfer (String bearerTokenNoSplit, MoneyTransferReq moneyTransferReq){
        String bearerToken = bearerTokenNoSplit.substring(7);
        // Transfers money from one account to another.
        // Transfers can occur simultaneously.
        try {
            if (isOwner(bearerToken,moneyTransferReq.getFrom())){
                if (hasEnoughAmaount(moneyTransferReq.getFrom(),moneyTransferReq.getAmount())){
                        // transfer money
                    Optional<AccountModel> accountFrom = accountModelRepository.findById(moneyTransferReq.getFrom());
                    Optional<AccountModel> accountTo = accountModelRepository.findById(moneyTransferReq.getTo());
                    if(accountFrom.isPresent() && accountTo.isPresent()){
                        accountFrom.get().setBalance(accountFrom.get().getBalance().subtract(moneyTransferReq.getAmount()));
                        accountTo.get().setBalance(accountTo.get().getBalance().add(moneyTransferReq.getAmount()));
                        //transactionModelRepository.save(createTransaction(moneyTransferReq,SUCCESS));
                        //transactionModelRepository.save(successTransaction(moneyTransferReq));
                        return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithoutModel("Money transferred"));
                    }else
                       // transactionModelRepository.save(createTransaction(moneyTransferReq,FAILED));
                        return ResponseEntity.status(402).body(responseModelUtil.errorUserResponse("You are not the owner"));
                }else{
                   // transactionModelRepository.save(createTransaction(moneyTransferReq,FAILED));
                    return ResponseEntity.status(401).body(responseModelUtil.errorUserResponse("You dont have enough money"));
                }
            }else{
               // transactionModelRepository.save(createTransaction(moneyTransferReq,FAILED));
                return ResponseEntity.status(402).body(responseModelUtil.errorUserResponse("You are not the owner"));
            }
        }catch (Exception e){
            //transactionModelRepository.save(createTransaction(moneyTransferReq,FAILED));
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }

    }

    public ResponseEntity<?> viewTransactionHistory (String bearerToken,UUID accountId){
        try {
            if(isOwnerWithAccountId(bearerToken,accountId)) {
                // List<TransactionModel> transactionFromList = transactionModelRepository.findAllByFrom(String.valueOf(accountId));
                // List<TransactionModel> transactionToList = transactionModelRepository.findAllByTo(String.valueOf(accountId));
                List<TransactionModel> allTransactionList = transactionModelRepository.findAllByToOrFrom(String.valueOf(accountId),String.valueOf(accountId));
                if (allTransactionList.isEmpty()) {
                    return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("No transaction found"));
                } else {
                    return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithModel("All Transactions Listed", allTransactionList));
                }
            }else{
                return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("You are not the account owner"));
            }

        }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }

    }

    private boolean isOwner (String bearerToken, UUID from){
    Optional<UserModel> userFromToken = userModelRepository.findByUsername(jwtTokenProvider.getUsername(bearerToken));
    Optional<AccountModel> accountModel = accountModelRepository.findById(from);
    if(userFromToken.isPresent() && accountModel.isPresent()){
        return userFromToken.get().getUserId().equals(accountModel.get().getUser().getUserId());
    }
    else{
        return false;
    }
    }

    private boolean hasEnoughAmaount (UUID from, BigDecimal amaount){
       Optional<AccountModel> fromAccount = accountModelRepository.findById(from);
        if(fromAccount.isPresent()){
            return fromAccount.get().getBalance().compareTo(amaount) >= 0;
        }
        else{
            return false;
        }
    }

    private TransactionModel createTransaction (MoneyTransferReq moneyTransferReq, TransactionStatus status){
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setFrom(String.valueOf(moneyTransferReq.getFrom()));
        transactionModel.setTo(String.valueOf(moneyTransferReq.getTo()));
        transactionModel.setAmount(moneyTransferReq.getAmount());
        transactionModel.setTransactionDate(LocalDateTime.now());
        transactionModel.setStatus(status);
        return transactionModel;
    }

    private TransactionModel successTransaction (MoneyTransferReq moneyTransferReq){
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setFrom(String.valueOf(moneyTransferReq.getFrom()));
        transactionModel.setTo(String.valueOf(moneyTransferReq.getTo()));
        transactionModel.setAmount(moneyTransferReq.getAmount());
        transactionModel.setTransactionDate(LocalDateTime.now());
        transactionModel.setStatus(SUCCESS);
        return transactionModel;
    }

    private TransactionModel failTransaction (MoneyTransferReq moneyTransferReq){
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setFrom(String.valueOf(moneyTransferReq.getFrom()));
        transactionModel.setTo(String.valueOf(moneyTransferReq.getTo()));
        transactionModel.setAmount(moneyTransferReq.getAmount());
        transactionModel.setTransactionDate(LocalDateTime.now());
        transactionModel.setStatus(FAILED);
        return transactionModel;
    }

    private boolean isOwnerWithAccountId (String bearerToken, UUID accountId){
        Optional<UserModel> userFromToken = userModelRepository.findByUsername(jwtTokenProvider.getUsername(bearerToken));
        Optional<AccountModel> accountModel = accountModelRepository.findById(accountId);
        if(userFromToken.isPresent()&& accountModel.isPresent() ){
            return userFromToken.get().getUserId().equals(accountModel.get().getUser().getUserId());
        }
        else{
            return false;
        }
    }

}
