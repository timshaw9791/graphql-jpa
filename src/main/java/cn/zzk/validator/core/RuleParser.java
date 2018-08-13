package cn.zzk.validator.core;


import cn.zzk.validator.exceptions.StringLogicException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MutationValidation 的解析器
 */
public class RuleParser {

    /**
     * 属性获取的正则表达式
     */
    private static final String PROPERTY_REGEX = "[a-z][a-zA-Z0-9_$]*";

    /**
     * 属性检查的正则表达式
     */
    private static final String PROPERTY_CHECK_REGEX = "[a-z][a-zA-Z0-9_$]*";


    /**
     * 根据校验规则缓存属性名
     */
    private static final Map<String, List<String>> rulePropertyCache = new HashMap<>();


    /**
     * spEL 解析器，用来解析 String 并执行String语法，这里用来解析并执行 String 的逻辑判断
     */
    public static final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 解析 校验规则中的属性或参数
     */
    public static List<String> getProperties(String rule) {

        if (rulePropertyCache.containsKey(rule)) {
            return rulePropertyCache.get(rule);
        }


        Pattern pattern = Pattern.compile(PROPERTY_REGEX);
        Matcher matcher = pattern.matcher(rule);

        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        checkStringLogic(rule, list);
        rulePropertyCache.put(rule, list);

        return list;
    }


    /**
     * 判断一个值是否为基本的对象。
     * 基本对象的定义 ： 若一个值是基本数据类型、基本数据类型的包装类、String中的一种，则视为基本对象
     * 实质则为是否可以用单个 键值对来表示该对象。
     *
     * @param value 要判断的值
     * @return true 则是 基本对象，false则不是
     */
    public static boolean isSimpleObject(Object value) {
        return value.getClass().isPrimitive()
                || value instanceof String
                || value instanceof Integer
                || value instanceof Double
                || value instanceof Long
                || value instanceof Float
                || value instanceof Short
                || value instanceof Character
                || value instanceof Byte
                || value instanceof Boolean;
    }

    /**
     * 判断指定的类型是否是简单对象的类型。
     * 基本对象的定义 ： 若一个值是基本数据类型、基本数据类型的包装类、String中的一种，则视为基本对象
     * 实质则为是否可以用单个 键值对来表示该对象。
     *
     * @param clz 要判断的类型
     * @return true 则是 基本对象，false则不是
     */
    public static boolean isSimpleObject(Class clz) {
        return clz.isPrimitive()
                || clz == String.class
                || clz == Integer.class
                || clz == Double.class
                || clz == Long.class
                || clz == Float.class
                || clz == Short.class
                || clz == Character.class
                || clz == Byte.class
                || clz == Boolean.class;
    }


    private static void checkStringLogic(String rule, List<String> properties) {
        if (!"".equals(rule)) {
            for (String property : properties) {
                rule = rule.replace(property, "true");
            }
            try {
                parser.parseRaw(rule).getValue(boolean.class);
            } catch (Exception ex) {
                throw new StringLogicException("字符串逻辑异常 : " + rule);
            }
        }

    }


}
