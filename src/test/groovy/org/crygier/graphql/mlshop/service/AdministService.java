package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.Administ;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Curtain
 * @date 2018/8/9 14:44
 */
public interface AdministService extends UserDetailsService {

    /**
     * 添加
     * @param administ
     * @return
     */
    Administ save(Administ administ);

    /**
     * 修改
     * @param administ
     * @return
     */
    Administ update(Administ administ);

    /**
     * 修改密码
     * @param administ
     * @param password
     * @return
     */
    Administ modifyPassword(Administ administ,String password);

}
