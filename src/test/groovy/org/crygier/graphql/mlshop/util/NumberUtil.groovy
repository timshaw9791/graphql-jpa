package org.crygier.graphql.mlshop.util;

/**
 * @author Curtain
 * @date 2018/8/2 10:59
 */

public class NumberUtil {

    public static String getNumber(){
       return String.valueOf(System.currentTimeMillis()).substring(1);
    }

    /**
     * 生成随机数
     * @param num 位数
     * @return
     */
    public static String createRandomNum(int num){
        String randomNumStr = "";
        for(int i = 0; i < num;i ++){
            int randomNum = (int)(Math.random() * 10);
            randomNumStr += randomNum;
        }
        return randomNumStr;
    }

}
