package org.crygier.graphql.mlshop.util;


/**
 * @author Curtain
 * @date 2018/5/16 15:48
 * 字符编码转化
 */
public class CharsetUtil {

    public final static String UN_LOGIN = "{\"code\":\"1\",\"msg\":\"对不起你还未登录，请先登录!\"}";

    public final static String AUTHENTICATION_FAIL = "{\"code\":\"1\",\"msg\":\"认证失败，用户名或密码错误!\"}";

    public final static String PERMISSION_DENIED = "{\"code\":\"1\",\"msg\":\"权限不足，请联系管理员!\"}";

    public final static String LOGOUT_SUCCESS = "{\"code\":\"0\",\"msg\":\"登出成功!\"}";


//    public static String charsetConvert(String charset){
//        try {
//            return new String(charset.getBytes("UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            return charset;
//        }
//    }
}
