package com.yunzhitx.java8.learning.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ClassName: Address <br/>
 * Description: <br/>
 * date: 2020/1/2 23:53<br/>
 *
 * @author 陈荣祥 <br/>
 * @since JDK 1.8
 */
@Data
@Accessors(chain = true)
public class Address {

    private String country;

    private String province;

    private String city;

    private String street;
}
