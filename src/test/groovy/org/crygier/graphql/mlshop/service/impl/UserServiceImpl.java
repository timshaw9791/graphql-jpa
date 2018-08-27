package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum;
import org.crygier.graphql.mlshop.model.user.User;
import org.crygier.graphql.mlshop.repo.CustomerRepository;
import org.crygier.graphql.mlshop.repo.UserRepository;
import org.crygier.graphql.mlshop.service.UserService;
import org.crygier.graphql.mlshop.util.VerifyUtil;
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

    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public User findByUsername(String username) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isPresent()) {
            return optional.get();
        } else return null;
    }

    @Override
    public User register(User user) {
        if (VerifyUtil.validity(user.getPhone() + VerifyUtil.REGISTER)) {
            //todo 如果手机号已经被注册  则。。。

            //创建客户信息
            Customer customer = new Customer();
            customer.setLevel(CustomerLevelEnum.C);
            customer.setTel(user.getPhone());
            customer = customerRepository.save(customer);
            user.setCustomer(customer);
            user.setUsername(user.getPhone());

            return userRepository.save(user);
        }
        throw new RuntimeException("验证码过期，请重新验证");
    }

    @Override
    public void modifyPassword(String password, String id) {
        User user = userRepository.findById(id).get();
        if (VerifyUtil.validity(user.getPhone()+VerifyUtil.MODIFY_PASSWORD)) {
            user.setPassword(password);
            userRepository.save(user);
        }
        throw new RuntimeException("验证码过期，请重新验证");
    }

    @Override
    public User findOne(String id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User update(User user, String id) {
        //判断登录的用户 和  修改的用户是不是同一个
        if (!(user.getId().equals(id))) {
            throw new RuntimeException("修改的用户信息 和  登录的不是一个");
        }
        User result = userRepository.findById(user.getId()).get();

        //判断客户信息id 是否相同
        if (!(result.getCustomer().getId().equals(user.getCustomer().getId()))) {
            throw new RuntimeException("修改的用户信息 和  登录的不是一个");
        }
        result.setHeadImg(user.getHeadImg());
        result.setNickname(user.getNickname());
        result.setCustomer(user.getCustomer());

        return userRepository.save(result);

    }

    @Override
    public void modifyPhone(String phone, String id) {
        User user = userRepository.findById(id).get();
        if (VerifyUtil.validity(user.getPhone()+VerifyUtil.MODIFY_PHONE)) {
            user.setPhone(phone);
            user.setUsername(phone);
            userRepository.save(user);
        }
        throw new RuntimeException("验证码过期，请重新验证");
    }
}
