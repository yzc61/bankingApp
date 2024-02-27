package com.yzc.bankingapp.controller;

import com.yzc.bankingapp.model.CreateAccountReq;
import com.yzc.bankingapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;


    @PostMapping
    public ResponseEntity<?> commonAccount (@RequestHeader(name = "Authorization", required = false) String bearerToken,@RequestBody(required = false) CreateAccountReq createAccountReq,@RequestParam(name = "number", required = false) String number,@RequestParam(name = "name", required = false) String name ){
        // Creates a new account for the authenticated user.
        if ( createAccountReq != null ){
            return accountService.createAccount(bearerToken,createAccountReq);
        }else if (number != null) {
            return accountService.getAccountByNumber(number);
        } else if (name != null) {
            return accountService.getAccountByAccountName(name);
        } else {
            return accountService.getUserAccounts(bearerToken);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount (@RequestHeader("Authorization") String bearerToken,@RequestBody CreateAccountReq createAccountReq){
        // Creates a new account for the authenticated user.

        return accountService.createAccount(bearerToken,createAccountReq);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchAccount (@RequestParam(name = "number", required = false) String number,@RequestParam(name = "name", required = false) String name ){
        // Search accounts for the authenticated user.
        // Accounts should be filterable on number and name.

        if (number != null) {
            return accountService.getAccountByNumber(number);
        } else if (name != null) {
            return accountService.getAccountByAccountName(name);
        } else {
            return accountService.getAllAccounts();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount (@PathVariable("id") UUID id, @RequestParam("name") String name){
        // Updates the selected account for the authenticated user.
        return accountService.updateAccount(id,name);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount (@PathVariable("id") UUID id){
        // Deletes the selected account for the authenticated user.
        return accountService.deleteAccount(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewAccountDetails (@RequestHeader("Authorization") String bearerToken, @PathVariable("id") UUID id){
        // Retrieves details of a specific account, including the balance. Access is restricted
        //    to the account owner.
        return accountService.viewAccountDetails(bearerToken, id);
    }

}
