package com.yzc.bankingapp.repository;

import com.yzc.bankingapp.model.AccountModel;
import com.yzc.bankingapp.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountModelRepository extends JpaRepository<AccountModel, UUID> {
    Optional<AccountModel> findByName(String name);
    Optional<AccountModel> findByNumber(String number);
    List<AccountModel> findAllByUser(UserModel user);
}
