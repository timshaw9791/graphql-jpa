package cn.zzk.validator.events;

import cn.zzk.validator.anntations.DomainRule;
import cn.zzk.validator.anntations.ValidSelect;
import cn.zzk.validator.core.RuleParser;
import cn.zzk.validator.exceptions.DomainRuleCheckException;
import cn.zzk.validator.exceptions.ValidSelectCheckException;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 对所有的 @SelectValid 与 @DomainRule 的实例进行检查
 */
@Component
public class RuleValidCheckEvent implements ApplicationListener<ApplicationStartedEvent> {


    private static final ParameterNameDiscoverer parameterNameDiscoverer =
            new LocalVariableTableParameterNameDiscoverer();


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        validSelectCheck(event);
        domainRuleCheck(event);
    }


    /**
     * 针对 @RestController 注解的类下的方法下的包含 @DomainRule 的参数进行 rule 进行检查。
     * 具体检查 rule 中的属性是否与 @DomainRule 注解的对象的属性是否匹配
     *
     * @throws DomainRuleCheckException @DomainRule 的实例的 rule 中的 property 与注解对应的 对象不匹配
     */
    private static void domainRuleCheck(ApplicationStartedEvent event) throws DomainRuleCheckException {
        event.getApplicationContext()
                .getBeansWithAnnotation(Validated.class)
                .values()
                .stream()
                .map(AopUtils::getTargetClass)
                .map(Class::getMethods)
                .flatMap(methods -> Arrays.stream(methods))
                .forEach(method -> {
                    Parameter[] parameters = method.getParameters();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    for (int i = 0; i < parameters.length; i++) {
                        //找到了有 @DomainRule 注解的参数
                        if (parameters[i].isAnnotationPresent(DomainRule.class)) {
                            String rule = parameters[i].getAnnotation(DomainRule.class).value();
                            Class target = parameterTypes[i];

                            List<String> properties = RuleParser.getProperties(rule);
                            List<String> fields = Arrays.stream(target.getDeclaredFields())
                                    .map(Field::getName)
                                    .collect(Collectors.toList());

                            if (!fields.containsAll(properties)) {
                                String message = "@DomainRule 中的属性与方法参数不匹配," + "\n" +
                                        "rule :" + rule + "\n" +
                                        " method : " + method.toString();
                                throw new DomainRuleCheckException(message);
                            }
                        }
                    }
                });
    }


    /**
     * 针对 @RestController 注解的类下的包含 @ValidSelect  的参数方法进行 rule 进行检查。
     * 具体检查 rule 中的 参数是否与 方法中的参数名匹配。
     *
     * @throws ValidSelectCheckException @ValidSelect 中的 rule 与 中的参数名不匹配
     */
    private static void validSelectCheck(ApplicationStartedEvent event) throws ValidSelectCheckException {
        Optional<Method> errorMethod = event.getApplicationContext()
                .getBeansWithAnnotation(Validated.class)
                .values()
                .stream()
                .map(AopUtils::getTargetClass)
                .map(Class::getMethods)
                .flatMap(methods -> Arrays.stream(methods))
                .filter(method -> method.isAnnotationPresent(ValidSelect.class))
                .filter(method -> {
                    List<String> params = Arrays.asList(parameterNameDiscoverer.getParameterNames(method));

                    return Arrays.stream(method.getAnnotationsByType(ValidSelect.class))
                            .noneMatch(validSelect -> {
                                String rule = validSelect.value();
                                List<String> properties = RuleParser.getProperties(rule);
                                return params.containsAll(properties);
                            });
                }).findAny();

        if (errorMethod.isPresent()) {
            String message = "@ValidSelect 中的参数与方法中的参数不匹配 "
                    + errorMethod.get().toString();
            throw new ValidSelectCheckException(message);
        }
    }


}
