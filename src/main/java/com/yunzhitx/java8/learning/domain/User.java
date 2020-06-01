package com.yunzhitx.java8.learning.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * ClassName: User <br/>
 * Description: <br/>
 * date: 2019/12/23 15:34<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
@Data
@Accessors(chain = true)
public class User {

    /**
     * id
     */
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 职位
     */
    private String job;

    /**
     * 积分
     */
    private Integer score;

    /**
     * 爱好
     */
    private List<String> hobbies;

    /**
     * 住址
     */
    private Address address;
}
