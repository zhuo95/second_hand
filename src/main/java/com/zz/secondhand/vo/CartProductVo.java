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

    private Long userId; //对方userid

    private String name;

    private String image;

    private BigDecimal price;

    private String boughtTime;
}
