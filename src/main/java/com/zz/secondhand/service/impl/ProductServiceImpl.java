package com.zz.secondhand.service.impl;


import com.zz.secondhand.common.Const;
import com.zz.secondhand.common.ResponseCode;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.Category;
import com.zz.secondhand.entity.Product;
import com.zz.secondhand.entity.Transaction;
import com.zz.secondhand.entity.User;
import com.zz.secondhand.repository.CategoryRepository;
import com.zz.secondhand.repository.ProductRepository;
import com.zz.secondhand.repository.TransactionRepository;
import com.zz.secondhand.repository.UserRepository;
import com.zz.secondhand.service.ICategoryService;
import com.zz.secondhand.service.IProductService;
import com.zz.secondhand.util.DateTimeUtil;
import com.zz.secondhand.util.FTPUtil;
import com.zz.secondhand.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("IProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;



    public ServerResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if(product==null){
            return ServerResponse.creatByErrorMessage("找不到该商品");
        }
        ProductDetailVo productDetailVo = assembleProducDetail(product);
        if(productDetailVo==null) return ServerResponse.creatByErrorMessage("查找商品有错误，找不到卖家信息!");
        return ServerResponse.creatBySuccess(productDetailVo);
    }
//组装VO
    private ProductDetailVo assembleProducDetail(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setImage(product.getImage());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        //查找根节点
        Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
        if(category==null){
            productDetailVo.setParentCategoryId(0);//根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        User user = userRepository.findById(product.getUserId()).orElse(null);
        if(user==null){
            return null;
        }
        productDetailVo.setAvatar(user.getAvatar());
        productDetailVo.setSellerInfo(user.getContactInfo());
        productDetailVo.setSellerName(user.getNickName());
        return productDetailVo;
    }

    //按关键词和categoryId 搜索
    public ServerResponse<Page> list(String keyword,Integer categoryId, int pageIndex,int pageSize){
        if(StringUtils.isBlank(keyword) && categoryId==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //放搜索到的子分类
        List<Integer> categoryIdList = new ArrayList<>();
        if(categoryId!=null){
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if(category==null && StringUtils.isBlank(keyword)){
                return ServerResponse.creatByErrorMessage("没有该商品");
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        //两端加百分号, e.g. WHERE CustomerName LIKE '%or%'	Finds any values that have "or" in any position
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
            Sort sort =  new Sort(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
            Page<Product> products = productRepository.findAllByNameLikeAndStatus(keyword,pageable,Const.ProductStatus.PRODUCT_ON_SALE);
            return ServerResponse.creatBySuccess(products);
        }
        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        Page<Product> products = productRepository.findAllByCategoryIdInAndStatus(categoryIdList,Const.ProductStatus.PRODUCT_ON_SALE,pageable);
        return ServerResponse.creatBySuccess(products);
    }

    //listAll
    public ServerResponse<Page> listAll(int pageIndex,int pageSize ){
        Sort sort =  new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageIndex,pageSize,sort);
        Page<Product> products = productRepository.findAllByStatus(Const.ProductStatus.PRODUCT_ON_SALE,pageable);
        return ServerResponse.creatBySuccess(products);
    }

    //出售
    public ServerResponse sell(Product product,User user){
        if(product.getName()==null||product.getCategoryId()==null||product.getPrice()==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        product.setStatus(Const.ProductStatus.PRODUCT_ON_SALE);
        product.setUserId(user.getId());
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        productRepository.save(product);
        ProductDetailVo productDetailVo = assembleProducDetail(product);
        //加上用户信息
        productDetailVo.setSellerInfo(user.getContactInfo());
        productDetailVo.setAvatar(user.getAvatar());
        productDetailVo.setSellerName(user.getUsername());
        return ServerResponse.creatBySuccess(productDetailVo);
    }

    //下架
    public ServerResponse del(Long productId,Long userId){
        Product product = productRepository.findById(productId).orElse(null);
        if(product==null) return ServerResponse.creatByErrorMessage("没有该商品");
        if(!product.getUserId().equals(userId)){
            return ServerResponse.creatByErrorMessage("没有权限操作");
        }
        //下架
        product.setStatus(Const.ProductStatus.PRODUCT_NOT_SALE);
        product.setUpdateTime(new Date());
        productRepository.save(product);
        //删除图片
        //if(!FTPUtil.deleteFile(product.getImage())) return ServerResponse.creatByErrorMessage("图片删除失败");
        return ServerResponse.creatBySuccessMessage("操作成功");
    }

    //购买
    public ServerResponse buyByProductId(Long productId,Long userId){
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null) return ServerResponse.creatByErrorMessage("没有该商品");
        if(product.getStatus()==Const.ProductStatus.PRODUCT_NOT_SALE) return ServerResponse.creatByErrorMessage("该物品没有出售");
        //设置product 下架
        product.setStatus(Const.ProductStatus.PRODUCT_NOT_SALE);
        product.setUpdateTime(new Date());
        productRepository.save(product);

        Transaction transaction = new Transaction();
        transaction.setProductId(productId);
        transaction.setBoughtUserId(userId);
        transaction.setSoldUserId(product.getUserId());
        transaction.setCreateTime(new Date());
        transaction.setStatus(Const.TransactionStatus.TRANSACTION_NOT_COMPLETE);
        transactionRepository.save(transaction);
        return ServerResponse.creatBySuccess("操作成功");
    }

    //取消卖给某人
    public ServerResponse cancelTransaction(Long productId,Long userId){
        Transaction transaction = transactionRepository.findByProductId(productId);
        if(transaction==null) return ServerResponse.creatByErrorMessage("没有该交易");
        if(!transaction.getBoughtUserId().equals(userId)&&!transaction.getSoldUserId().equals(userId)){
            return ServerResponse.creatByErrorMessage("无权操作");
        }
        Product p = productRepository.findById(transaction.getProductId()).orElse(null);
        if(p==null) return ServerResponse.creatByErrorMessage("未找到该商品");
        p.setStatus(Const.ProductStatus.PRODUCT_ON_SALE);
        p.setUpdateTime(new Date());
        productRepository.save(p);

        transactionRepository.delete(transaction);
        return ServerResponse.creatBySuccessMessage("取消成功");
    }

    //完成交易
    public ServerResponse finishTransaction(Long productId,Long userId){
        Transaction transaction = transactionRepository.findByProductId(productId);
        if(transaction==null) return ServerResponse.creatByErrorMessage("没有该交易");
        if(!transaction.getSoldUserId().equals(userId)){
            return ServerResponse.creatByErrorMessage("无权操作");
        }
        Product p = productRepository.findById(transaction.getProductId()).orElse(null);
        if(p==null) return ServerResponse.creatByErrorMessage("未找到该商品");
        p.setStatus(Const.ProductStatus.PRODUCT_NOT_SALE);
        p.setUpdateTime(new Date());
        productRepository.save(p);
        transaction.setStatus(Const.TransactionStatus.TRANSACTION_COMPLETE);
        transactionRepository.save(transaction);
        return ServerResponse.creatByErrorMessage("交易成功");
    }


}
