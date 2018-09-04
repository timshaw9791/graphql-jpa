package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.exception.MLShopRunTimeException;
import org.crygier.graphql.mlshop.model.Administ;
import org.crygier.graphql.mlshop.repo.AdministRepository;
import org.crygier.graphql.mlshop.service.AdministService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/8/9 14:48
 */

@Service
public class AdministServiceImpl implements AdministService {

    @Autowired
    private AdministRepository administRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Administ> optional = administRepository.findByUsername(Optional.ofNullable(s).orElse(""));
        if (!optional.isPresent()) {
//            throw new UsernameNotFoundException(ResultExceptionEnum.ROLE_IS_EXIST.getMessage());
            new MLShopRunTimeException("用户不存在，请重新确认你的账号名是否正确");
        }
        return optional.get();
    }

    @Override
    public Administ save(Administ administ) {
        //todo  密码加密
        Optional<Administ> administOptional = administRepository.findByUsername(administ.getUsername());
        if (administOptional.isPresent()){
            throw new MLShopRunTimeException("用户已存在,请重新填写用户名");
        }

        return administRepository.save(administ);
    }

    @Override
    public Administ update(Administ administ) {
        Administ result = administRepository.findById(administ.getId()).get();
        if (result.getPassword()==null){
            administ.setPassword(null);
        }else {
            administ.setPassword(result.getPassword());
        }
        return administRepository.save(administ);
    }

    @Override
    public Administ modifyPassword(Administ administ) {
        Optional<Administ> optional = administRepository.findById(administ.getId());
        if (optional.isPresent()){
            Administ result = optional.get();
            result.setPassword(administ.getPassword());
            return administRepository.save(result);
        }
       else {
            throw new MLShopRunTimeException("未找到用户,修改密码失败");
        }
    }
}
