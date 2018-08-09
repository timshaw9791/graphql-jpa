package org.crygier.graphql.mlshop.service.impl;

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
            new RuntimeException("no user");
        }
        return optional.get();
    }
}
