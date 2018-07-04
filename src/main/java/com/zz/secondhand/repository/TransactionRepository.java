package com.zz.secondhand.repository;

import com.zz.secondhand.entity.Product;
import com.zz.secondhand.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TransactionRepository  extends JpaRepository<Transaction,Integer> {
    Transaction findByProductId(Long productId);

    List<Transaction> findAllByBoughtUserId(Long boughtUserId);

    List<Transaction> findAllByCreateTimeBeforeAndStatus(Date date,int status);
}
