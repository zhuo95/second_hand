package com.zz.secondhand.service.impl;

import com.google.common.collect.Lists;
import com.zz.secondhand.common.Const;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.Product;
import com.zz.secondhand.entity.Transaction;
import com.zz.secondhand.repository.ProductRepository;
import com.zz.secondhand.repository.TransactionRepository;
import com.zz.secondhand.service.ICartService;
import com.zz.secondhand.util.DateTimeUtil;
import com.zz.secondhand.vo.CartProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("ICartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ProductRepository productRepository;

    //查找当前账号的卖的物品
    public ServerResponse<Page> getSold(Long userId, int pageIndex, int pageSize){
        List<Product> products = productRepository.findAllByUserId(userId);

        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        List<CartProductVo> soldProductVos = Lists.newArrayList();
        for(Product p : products){
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setProductId(p.getId());
            cartProductVo.setImage(p.getImage());
            cartProductVo.setName(p.getName());
            cartProductVo.setPrice(p.getPrice());
            cartProductVo.setUserId(p.getUserId());
            Transaction transaction = transactionRepository.findByProductId(p.getId());
            cartProductVo.setBoughtTime(DateTimeUtil.dateToStr(transaction.getCreateTime()));
            soldProductVos.add(cartProductVo);
        }
        Page<CartProductVo> page = new PageImpl<CartProductVo>(soldProductVos,pageable,soldProductVos.size());

        return ServerResponse.creatBySuccess(page);

    }

    //查找当前账号买的东西
    public ServerResponse<Page> getBought(Long userId,int pageIndex, int pageSize){
        List<Transaction> transactions = transactionRepository.findAllByBoughtUserId(userId);

        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        List<CartProductVo> cartProductVos = Lists.newArrayList();
        for(Transaction t : transactions){
            Long productId = t.getProductId();
            Product p  =  productRepository.findById(productId).orElse(null);

            if(p!=null){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setProductId(p.getId());
                cartProductVo.setImage(p.getImage());
                cartProductVo.setName(p.getName());
                cartProductVo.setPrice(p.getPrice());
                cartProductVo.setUserId(p.getUserId());
                cartProductVo.setBoughtTime(DateTimeUtil.dateToStr(t.getCreateTime()));
                cartProductVos.add(cartProductVo);
            }

        }
        Page<CartProductVo> page = new PageImpl<CartProductVo>(cartProductVos,pageable, cartProductVos.size());

        return ServerResponse.creatBySuccess(page);
    }

    //取消购买
    public ServerResponse cancelOrder(Long userId,Long productId){
        Transaction transaction = transactionRepository.findByProductId(userId);
        if(transaction==null||!transaction.getBoughtUserId().equals(userId)) return ServerResponse.creatByErrorMessage("您没有购买该物品");

        //delete逻辑
        transactionRepository.delete(transaction);
        Product product = productRepository.findById(productId).orElse(null);
        if(product==null) return ServerResponse.creatByErrorMessage("找不到该商品");
        product.setStatus(Const.ProductStatus.PRODUCT_ON_SALE);
        product.setUpdateTime(new Date());
        productRepository.save(product);
        return ServerResponse.creatBySuccessMessage("操作成功");
    }

}
