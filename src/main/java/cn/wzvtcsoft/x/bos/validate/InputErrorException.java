package cn.wzvtcsoft.x.bos.validate;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用来做输入错误提示
 */
public class InputErrorException extends RuntimeException implements GraphQLError {

    Map<String,Object> messageRelatePropsMap=new HashMap<>();

    public InputErrorException(Map<String, String[]> messageRelatePropsMap) {
        super("输入数据错误");
        messageRelatePropsMap.entrySet().stream().forEach(e ->
                this.messageRelatePropsMap.put(e.getKey(), StringUtils.collectionToCommaDelimitedString(Arrays.asList(e.getValue()))));
    }



    @Override
    public Map<String, Object> getExtensions() {
        return this.messageRelatePropsMap;
    }

    static final class InputError{
        private String messasge;
        private String[] relateProps;

        public String getMessasge() {
            return messasge;
        }

        public String[] getRelateProps() {
            return relateProps;
        }

        private  InputError(String message, String[] relateProp){
            this.messasge=message;
            this.relateProps=relateProp;
        }
    }


    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }
    @Override
    public ErrorType getErrorType() {
        return null;
    }

    @Override
    public List<Object> getPath() {
        return null;
    }

    @Override
    public Map<String, Object> toSpecification() {
        return null;
    }




}
