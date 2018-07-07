package com.zz.secondhand.repository;

import com.zz.secondhand.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product,Long> {

    Page<Product> findAllByNameLikeAndStatus(String keyword, Pageable pageable,int status);

    Page<Product> findAllByCategoryIdInAndStatus(List<Integer> categoryIds,int status,Pageable pageable);

    List<Product> findAllByUserIdAndStatus(Long userId,int status);

    Page<Product> findAllByStatus(int status,Pageable pageable);

}
