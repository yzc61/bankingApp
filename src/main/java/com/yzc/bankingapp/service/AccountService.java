package com.yzc.bankingapp.service;

import com.yzc.bankingapp.ResponseModelUtil;
import com.yzc.bankingapp.model.AccountModel;
import com.yzc.bankingapp.model.CreateAccountReq;
import com.yzc.bankingapp.model.ResponseAccount;
import com.yzc.bankingapp.model.UserModel;
import com.yzc.bankingapp.repository.AccountModelRepository;
import com.yzc.bankingapp.repository.UserModelRepository;
import com.yzc.bankingapp.security.JwtTokenProvider;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AccountModelRepository accountModelRepository;

    @Autowired
    ResponseModelUtil responseModelUtil;

    @Autowired
    UserModelRepository userModelRepository;

    public ResponseEntity<?> createAccount (String bearerToken, CreateAccountReq createAccountReq){
        // Creates a new account for the authenticated user.
        try {
            Optional<AccountModel> tempAccountByName = accountModelRepository.findByName(createAccountReq.getName());
            Optional<AccountModel> tempAccountByNumber = accountModelRepository.findByNumber(createAccountReq.getNumber());
            String username = jwtTokenProvider.getUsername(bearerToken.substring(7));
            Optional<UserModel> tempUser = userModelRepository.findByUsername(username);
            if (tempAccountByName.isEmpty() && tempUser.isPresent() && tempAccountByNumber.isEmpty()){
                // create Account
            AccountModel saveAccount = createAccountPart(createAccountReq, tempUser.get());
            accountModelRepository.save(saveAccount);
                return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithoutModel("Account created"));
            }else if(tempAccountByName.isPresent()){
                // name kullanılıyor
                return ResponseEntity.status(401).body(responseModelUtil.errorUserResponse("Account name already exists"));
            }else if(tempAccountByNumber.isPresent()){
                // name kullanılıyor
                return ResponseEntity.status(402).body(responseModelUtil.errorUserResponse("Account number already exists"));
            }else{
                return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("User not found"));
            }
        }catch (Exception e){

            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }
    }



    public ResponseEntity<?> getAccountByNumber (String number){
        try {
            Optional<AccountModel> tempAccount = accountModelRepository.findByNumber(number);
            if (tempAccount.isPresent()) {
                ResponseAccount responseAccount = new ResponseAccount(tempAccount.get());
                return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithModel("Account Listed", responseAccount));
            }else {
                return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("No Account"));
            }
            }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }
    }

    public ResponseEntity<?> getAccountByAccountName (String name){
        try {
            Optional<AccountModel> tempAccount = accountModelRepository.findByName(name);
            if (tempAccount.isPresent()) {
                ResponseAccount responseAccount = new ResponseAccount(tempAccount.get());
                return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithModel("Account Listed", responseAccount));
            }else {
                return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("No Account"));
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }
    }

    public ResponseEntity<?> getAllAccounts (){
        try {
            ArrayList<AccountModel> tempAccountList = (ArrayList<AccountModel>) accountModelRepository.findAll();
            ArrayList<ResponseAccount> responseAccounts = new ArrayList<>();
            for (AccountModel temp:tempAccountList) {
                ResponseAccount responseAccount = new ResponseAccount(temp);
                responseAccounts.add(responseAccount);
            }
            return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithModel("Accounts Listed",responseAccounts));
        }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }
    }

    public ResponseEntity<?> getUserAccounts (String bearerToken){
        try {
            Optional<UserModel> tempUser = userModelRepository.findByUsername(jwtTokenProvider.getUsername(bearerToken.substring(7)));
            if(tempUser.isPresent()) {
                ArrayList<AccountModel> tempAccountList = (ArrayList<AccountModel>) accountModelRepository.findAllByUser(tempUser.get());
                ArrayList<ResponseAccount> responseAccounts = new ArrayList<>();
                for (AccountModel temp : tempAccountList) {
                    ResponseAccount responseAccount = new ResponseAccount(temp);
                    responseAccounts.add(responseAccount);
                }
                return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithModel("Accounts Listed", responseAccounts));
            }else{
                return ResponseEntity.status(401).body(responseModelUtil.errorUserResponse("User not found"));
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }
    }

    public ResponseEntity<?> updateAccount (UUID id,String name){
        try {
          Optional<AccountModel> tempAccount =   accountModelRepository.findById(id);
          if(tempAccount.isPresent()){
              tempAccount.get().setName(name);
              accountModelRepository.save(tempAccount.get());
              return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithoutModel("Account updated"));
          } else{
              return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("Account not found"));
          }
        }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }
    }


    public ResponseEntity<?> deleteAccount (UUID id){
        try {
            Optional<AccountModel> tempAccount =   accountModelRepository.findById(id);
            if(tempAccount.isPresent()){
                accountModelRepository.delete(tempAccount.get());
                return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithoutModel("Account deleted"));
            } else{
                return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("Account not found"));
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }
    }


    public ResponseEntity<?> viewAccountDetails (String bearerToken,UUID id) {
        try {
            Optional<AccountModel> tempAccount = accountModelRepository.findById(id);
            if (tempAccount.isPresent()) {
                if (isOwner(tempAccount.get(),bearerToken)) {
                    ResponseAccount responseAccount = new ResponseAccount(tempAccount.get());
                    return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithModel("Account Detail Listed",responseAccount));
                } else {
                    return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("You are not the owner"));
                }
            } else {
                return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("Account not found"));
            }
            }catch(Exception e){
                return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
            }
        }


    private boolean isOwner (AccountModel accountModel,String token){
        String usernameFromToken = jwtTokenProvider.getUsername(token);
        String usernameFromAccount = accountModel.getUser().getUsername();
        return usernameFromAccount.equals(usernameFromAccount);
    }

    private AccountModel createAccountPart(CreateAccountReq createAccountReq,UserModel user){
    AccountModel accountModel = new AccountModel();
    accountModel.setBalance(BigDecimal.valueOf(0));
    accountModel.setName(createAccountReq.getName());
    accountModel.setUser(user);
    accountModel.setNumber(createAccountReq.getNumber());
    accountModel.setCreatedAt(LocalDateTime.now());
    accountModel.setUpdatedAt(LocalDateTime.now());
    return accountModel;
    }

}
