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
public class CartProductVo {
    private Long productId;

    private String userInfo; //对方userid

    private String name;

    private String image;

    private BigDecimal price;

    private Boolean sold;

    private String boughtTime;
}
