package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.user.User;

/**
 * @author Curtain
 * @date 2018/8/7 11:21
 */
public interface UserService {

    /**
     * 注册
     * @param user
     * @return
     */
    User register(User user);

    //登录


}
