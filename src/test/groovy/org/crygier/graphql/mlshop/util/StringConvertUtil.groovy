package org.crygier.graphql.mlshop.util

/**
 * @author Curtain
 * @date 2018/7/28 11:37
 */
class StringConvertUtil {

    /**
     * 解决 后去的参数是 {xxx=xxx} 形式
     * @param id
     * @return
     */
    public static String getId(String id){
        return id;
       // id = id.replace("{","");
       // id = id.replace("}","");
       // return id.split("=")[1];
    }
}
