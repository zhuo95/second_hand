package com.zz.secondhand.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailVo {
    private Long id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String image;
    private BigDecimal price;
    private Integer status;
    private String createTime;
    private String updateTime;
    private Integer parentCategoryId;

    private String sellerInfo;
    private String avatar;
    private String sellerName;
}
