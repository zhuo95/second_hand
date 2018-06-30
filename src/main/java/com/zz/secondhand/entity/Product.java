package com.zz.secondhand.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; //卖家id

    private Integer categoryId;

    @Column(length = 100)
    private String name;

    @Column(length = 40)
    private String title;

    private String image;

    @Column(length = 200)
    private String detail;

    private BigDecimal price;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
