package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.user.User;
import org.crygier.graphql.mlshop.repo.UserRepository;
import org.crygier.graphql.mlshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Curtain
 * @date 2018/8/9 8:32
 */
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User register(User user) {
        return userRepository.save(user);
    }


}
