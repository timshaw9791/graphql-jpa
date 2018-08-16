package org.crygier.graphql.mlshop.aspect;

import cn.wzvtcsoft.x.bos.domain.BosEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.crygier.graphql.mlshop.config.ContextRefreshedEventListen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/8/16 9:57
 */

@Component
@Aspect
public class UpdateExcludeAspect {

    @Autowired
    ContextRefreshedEventListen refreshedEventListen;

    @Pointcut("@annotation(org.crygier.graphql.mlshop.anntations.Exclude)")
    public void pointCut() {
    }


    @Before("pointCut()")
    public void doBefore(JoinPoint joinPoint) {
        Optional<Object> argOptional = Arrays.stream(joinPoint.getArgs()).filter(arg->arg instanceof BosEntity).findFirst();
        if (!(argOptional.isPresent())){
            return;
        }
        BosEntity bosEntity = (BosEntity) argOptional.get();
        Repository repository = refreshedEventListen.getRepository(bosEntity.getClass());
        Optional resultOptional = ((CrudRepository) repository).findById(bosEntity.getId());
        if (!(resultOptional.isPresent())){
            throw new RuntimeException("Not find entity id is : " + bosEntity.getId());
        }

        BosEntity result = (BosEntity) resultOptional.get();

        //todo 暂时默认去覆盖 这几个字段   之后可以做成可选的  覆盖Exclude包含的
        bosEntity.setNumber(result.getNumber());
        bosEntity.setCreatetime(result.getCreatetime());
        bosEntity.setCreateactorid(result.getCreateactorid());



    }
}
