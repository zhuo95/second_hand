package com.zz.secondhand.repository;

import com.zz.secondhand.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product,Long> {

    Page<Product> findAllByCategoryIdInAndNameLikeAndStatus(List<Integer> categoryIds,String keyword, Pageable pageable,int status);

    List<Product> findAllByUserId(Long userId);

}
