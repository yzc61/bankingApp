package com.yzc.bankingapp.service;

import com.yzc.bankingapp.ResponseModelUtil;
import com.yzc.bankingapp.model.CreateUserReq;
import com.yzc.bankingapp.model.LoginUserReq;
import com.yzc.bankingapp.model.UserModel;
import com.yzc.bankingapp.repository.UserModelRepository;
import com.yzc.bankingapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserModelRepository userModelRepository;

    @Autowired
    ResponseModelUtil responseModelUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider jwtTokenProvider;


    public ResponseEntity<?> registerUser (CreateUserReq createUserReq){
        // Registers a new user with username, password, and email.
        try {
        Optional<UserModel> tempUser = userModelRepository.findByUsername(createUserReq.getUsername());

        if(tempUser.isEmpty()){
            UserModel saveUser = createUserModel(createUserReq);
            userModelRepository.save(saveUser);
            return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithoutModel("User registered"));
        }else{
            // username kullanılıyor
            return ResponseEntity.status(400).body(responseModelUtil.errorUserResponse("Username already exists"));
        }
        }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }

    }


    public ResponseEntity<?> userLogin (LoginUserReq loginUserReq){
        // Registers a new user with username, password, and email.
        try {
            Optional<UserModel> tempUser = userModelRepository.findByUsername(loginUserReq.getUsername());

            if(tempUser.isPresent()){

                if (passwordEncoder.matches(loginUserReq.getPassword(),tempUser.get().getPassword())) {

                    return ResponseEntity.status(200).body(responseModelUtil.succesResponseWithModel("User logged in", "Bearer " + jwtTokenProvider.createToken(tempUser.get())));
                }else{
                    // şifre yanlış
                    return ResponseEntity.status(401).body(responseModelUtil.errorUserResponse("Username or password not match"));
                }
            }else{
                // username yok
                return ResponseEntity.status(401).body(responseModelUtil.errorUserResponse("Username or password not match"));
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body(responseModelUtil.errorSystemResponse(e.getLocalizedMessage()));
        }

    }

    private UserModel createUserModel (CreateUserReq createUserReq){
        UserModel userModel = new UserModel();
        userModel.setUsername(createUserReq.getUsername());
        userModel.setPassword(passwordEncoder.encode(createUserReq.getPassword()));
        userModel.setEmail(createUserReq.getEmail());
        userModel.setCreatedAt(LocalDateTime.now());
        userModel.setUpdatedAt(LocalDateTime.now());
        return userModel;
    }


}

