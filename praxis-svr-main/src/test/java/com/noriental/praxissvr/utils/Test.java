package com.noriental.praxissvr.utils;

import java.math.BigDecimal;

/**
 * @author kate
 * @create 2018-05-16 17:05
 * @desc 自测
 **/
public class Test {

    public static void main(String[] args) {
       Double calculateScore=new Double(4.36);
        BigDecimal bg = new BigDecimal(calculateScore);
        calculateScore = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(calculateScore);
    }
}
