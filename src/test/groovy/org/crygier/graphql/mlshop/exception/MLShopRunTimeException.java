package org.crygier.graphql.mlshop.exception;

/**
 * 业务异常 通过MLShopRunTimeException体现
 * @author Curtain
 * @date 2018/9/4 13:52
 */
public class MLShopRunTimeException extends RuntimeException{

    public MLShopRunTimeException(String message){
        super(message);
    }
}
