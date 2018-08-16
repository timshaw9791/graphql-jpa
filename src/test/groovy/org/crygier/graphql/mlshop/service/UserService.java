package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.user.User;

/**
 * @author Curtain
 * @date 2018/8/7 11:21
 */
public interface UserService {

    /**
     * 通过用户名查找
     *
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 注册
     *
     * @param user
     * @return
     */
    User register(User user);

    /**
     * 修改密码
     *
     * @param password
     */
    void modifyPassword(String password, String id);

    /**
     * 更换手机号
     *
     * @param phone
     */
    void modifyPhone(String phone, String id);

}
