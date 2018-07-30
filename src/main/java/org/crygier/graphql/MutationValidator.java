package org.crygier.graphql;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * mutation 验证器的接口，当验证不成功时，throw MutationValidateException.
 * 实现该接口的对象必须是无状态的。
 */
public interface MutationValidator {

    public void validate(MutationValidationMetaInfo metaInfo);

    /**
     * mutation validator用来验证的元信息，包括该mutatation对应方法上的annotation数组，
     * 以及各个形参对应的annotation数组（数组中已经去除了RequestParam注解，既然key本身就是）
     * 以及各个形参对应实参的map
     */
    public interface MutationValidationMetaInfo {
        /**
         * 返回该方法上定义的注解
         *
         * @return
         */
        Annotation[] getMethodAnnotations();

        /**
         * 根据argumentName拿到对应的参数注解
         *
         * @param argumentName
         * @return
         */
        Annotation[] getArgumentAnnotations(String argumentName);

        /**
         * 根据argumentName拿到形参对应的实际参数
         *
         * @param argumentName
         * @return
         */
        Object getArgumentObject(String argumentName);
    }

    /**
     * 验证出错时需要跑出的异常，
     * messageRelatePropsMap，指的是如是某个形参出问题，则把对应的正面消息，以及涉及到的属性字符串形成一个entry
     * （如果涉及到多个属性，请用,号隔开）。如以下示例可能出现的entry有："注册校验错误"-> "user.password,name"
     *
     * @ValidSelect( message = "注册校验错误",value = "user && (age || name)")
     * @ValidSelect(value = "user && (age || name)", message = "注册校验错误")
     * @GetMapping("/test3") public void test3(@RequestParam("user") @DomainRule(value = "password && (email || phone)", message = "账号或密码错误") User user,
     * @Length(min = 2, max = 8)  @RequestParam("name") String name,
     * @Min(value=18) @RequestParam("age") int age) {
     * }
     */
    public class MutationValidateException extends RuntimeException {
        private final Map<String, String> messageRelatePropsMap;

        public MutationValidateException(Map<String, String> messageRelatePropsMap) {
            this.messageRelatePropsMap = messageRelatePropsMap;
        }

        public Map<String, String> getMessageRelatePropsMap() {
            return this.messageRelatePropsMap;
        }

    }
}

