package com.zz.secondhand.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //parentID是0的时候说明是根节点
    private Integer parentId;

    @Column(length = 40)
    private String name;

    private Boolean status;

    private Date createTime;

    private Date updateTime;
}
