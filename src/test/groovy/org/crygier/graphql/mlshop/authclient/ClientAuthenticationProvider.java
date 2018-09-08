package org.crygier.graphql.mlshop.authclient;

import org.crygier.graphql.mlshop.config.MyPasswordEncoder;
import org.crygier.graphql.mlshop.model.user.User;
import org.crygier.graphql.mlshop.service.UserService;
import org.crygier.graphql.mlshop.utils.SpringUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class ClientAuthenticationProvider implements AuthenticationProvider {



    public ClientAuthenticationProvider() {

    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ClientAuthenticationToken authenticationToken = (ClientAuthenticationToken) authentication;
        String principal = (String) authenticationToken.getPrincipal();
        String credentials = (String) authentication.getCredentials();

        UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");

        User user = userService.findByUsername(principal);
        if (user==null){
            throw new BadCredentialsException("User is not exist");
        }

        MyPasswordEncoder myPasswordEncoder = new MyPasswordEncoder();
        boolean matches = myPasswordEncoder.matches(credentials, user.getPassword());
        if (matches){

            ClientAuthenticationToken authenticationResult = new ClientAuthenticationToken(user.getId(),null,true);
            return authenticationResult;
        }
        throw new BadCredentialsException("Validation failed, password error.");
    }



    @Override
    public boolean supports(Class<?> authentication) {
        return ClientAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
