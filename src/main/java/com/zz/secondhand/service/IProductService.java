package com.zz.secondhand.service;

import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.Product;
import com.zz.secondhand.entity.User;
import org.springframework.data.domain.Page;

public interface IProductService {
    ServerResponse getProductDetail(Long prductId);

    ServerResponse<Page> list(String keyword, Integer categoryId, int pageIndex, int pageSize);

    ServerResponse sell(Product product,User user);

    ServerResponse del(Long productId,Long userId);

    ServerResponse buyByProductId(Long productId,Long userId);

    ServerResponse cancelTransaction(Long productId,Long userId);

    ServerResponse finishTransaction(Long productId,Long userId);

    ServerResponse<Page> listAll(int pageIndex,int pageSize );
}
