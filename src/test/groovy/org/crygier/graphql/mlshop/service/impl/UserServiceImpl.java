package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.user.User;
import org.crygier.graphql.mlshop.repo.UserRepository;
import org.crygier.graphql.mlshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/8/9 8:32
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public User findByUsername(String username) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isPresent()){
            return optional.get();
        }
        else return null;
    }

    @Override
    public void modifyPassword(String password) {

    }

    @Override
    public void modifyPhone(String phone) {

    }

    @Override
    public User register(User user) {
        return userRepository.save(user);
    }


}
