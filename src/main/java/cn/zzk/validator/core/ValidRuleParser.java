package cn.zzk.validator.core;

import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidRuleParser {

    private static final String PROPERTY_REGEX = "[a-z][a-zA-Z0-9_$]*";
    private static final Map<String, List<String>> propertyCache = new HashMap<>();
    private static final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 通过 regex 抓取校验规则的 所有属性
     */
    public static List<String> getProperties(String rule) {

        if (propertyCache.containsKey(rule)) {
            return propertyCache.get(rule);
        }


        Pattern pattern = Pattern.compile(PROPERTY_REGEX);
        Matcher matcher = pattern.matcher(rule);

        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        propertyCache.put(rule, list);

        return list;
    }




    /**
     * 获得 验证规则的 字符串类型 的逻辑判断
     */
    public static boolean executeStringValid(String rule, List<String> properties, List<Boolean> booleans) {
        if (properties.size() != booleans.size())
            throw new IllegalArgumentException("属性个的个数与验证结果的个数不正确");

        String result = rule;
        for (int i = 0; i < properties.size(); i++) {
            result = result.replace(properties.get(i), booleans.get(i).toString());
        }

        return parser.parseExpression(result).getValue(boolean.class);
    }
}
