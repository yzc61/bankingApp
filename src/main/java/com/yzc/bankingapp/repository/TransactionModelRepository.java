package com.yzc.bankingapp.repository;

import com.yzc.bankingapp.model.TransactionModel;
import com.yzc.bankingapp.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionModelRepository extends JpaRepository<TransactionModel, Long> {
    List<TransactionModel> findAllByFrom(String from);
    List<TransactionModel> findAllByTo(String to);
    List<TransactionModel> findAllByToOrFrom (String to, String from);
}
