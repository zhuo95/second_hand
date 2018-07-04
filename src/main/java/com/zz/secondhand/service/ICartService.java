package com.zz.secondhand.service;

import com.zz.secondhand.common.ServerResponse;
import org.springframework.data.domain.Page;

public interface ICartService {
    ServerResponse<Page> getSold(Long userId, int pageIndex, int pageSize);

    ServerResponse<Page> getBought(Long userId,int pageIndex, int pageSize);

    ServerResponse cancelOrder(Long userId,Long productId);

    void closeOrder(int day);
}
