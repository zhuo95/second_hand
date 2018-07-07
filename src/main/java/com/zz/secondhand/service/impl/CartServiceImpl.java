package com.zz.secondhand.service.impl;

import com.google.common.collect.Lists;
import com.zz.secondhand.common.Const;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.Product;
import com.zz.secondhand.entity.Transaction;
import com.zz.secondhand.entity.User;
import com.zz.secondhand.repository.ProductRepository;
import com.zz.secondhand.repository.TransactionRepository;
import com.zz.secondhand.repository.UserRepository;
import com.zz.secondhand.service.ICartService;
import com.zz.secondhand.util.DateTimeUtil;
import com.zz.secondhand.util.FTPUtil;
import com.zz.secondhand.vo.CartProductVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("ICartService")
@Slf4j
public class CartServiceImpl implements ICartService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    //查找当前账号的卖的物品
    public ServerResponse<Page> getSold(Long userId, int pageIndex, int pageSize){
        List<Product> products = productRepository.findAllByUserIdAndStatus(userId,Const.ProductStatus.PRODUCT_ON_SALE);

        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        List<CartProductVo> soldProductVos = Lists.newArrayList();
        for(Product p : products){
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setProductId(p.getId());
            cartProductVo.setImage(p.getImage());
            cartProductVo.setName(p.getName());
            cartProductVo.setPrice(p.getPrice());
            if(p.getStatus()==Const.ProductStatus.PRODUCT_NOT_SALE){
                Transaction transaction = transactionRepository.findByProductId(p.getId());
                if(transaction!=null) {
                    cartProductVo.setSold(true);
                    cartProductVo.setBoughtTime(DateTimeUtil.dateToStr(transaction.getCreateTime()));
                    Long bought = transaction.getBoughtUserId();
                    User user = userRepository.findById(bought).orElse(null);
                    if (user != null) cartProductVo.setUserInfo(user.getContactInfo());
                }
            }else{
                   cartProductVo.setBoughtTime("未出售");
                   cartProductVo.setUserInfo("未出售");
                   cartProductVo.setSold(false);
            }
            soldProductVos.add(cartProductVo);
        }
        Page<CartProductVo> page = new PageImpl<CartProductVo>(soldProductVos,pageable,soldProductVos.size());

        return ServerResponse.creatBySuccess(page);

    }

    //查找当前账号买的东西
    public ServerResponse<Page> getBought(Long userId,int pageIndex, int pageSize){
        List<Transaction> transactions = transactionRepository.findAllByBoughtUserIdAndStatus(userId,Const.TransactionStatus.TRANSACTION_NOT_COMPLETE);

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
                //设置卖家信息
                User soldUser = userRepository.findById(p.getUserId()).orElse(null);
                if(soldUser!=null) cartProductVo.setUserInfo(soldUser.getContactInfo());
                else cartProductVo.setUserInfo("找不到该用户");
                cartProductVo.setBoughtTime(DateTimeUtil.dateToStr(t.getCreateTime()));
                cartProductVos.add(cartProductVo);
            }

        }
        Page<CartProductVo> page = new PageImpl<CartProductVo>(cartProductVos,pageable, cartProductVos.size());

        return ServerResponse.creatBySuccess(page);
    }

    //取消购买
    public ServerResponse cancelOrder(Long userId,Long productId){
        Transaction transaction = transactionRepository.findByProductId(productId);
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


    //每天定时删除一些图片
    public void closeOrder(int day){
        Date time = DateUtils.addDays(new Date(),-day);
        List<Transaction> transactions = transactionRepository.findAllByCreateTimeBeforeAndStatus(time,Const.TransactionStatus.TRANSACTION_COMPLETE);
        for( Transaction t : transactions){
            Product p = productRepository.findById(t.getProductId()).orElse(null);
            if(p!=null){
               String imgUrl = p.getImage();
               try{
                   FTPUtil.deleteFile(imgUrl);
               }catch (Exception e){
                   log.error("定时删除图片error",e);
               }
            }
        }
    }

}
