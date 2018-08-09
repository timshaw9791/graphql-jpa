package org.crygier.graphql.mlshop.config;

import org.crygier.graphql.mlshop.util.CharsetUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * @author Curtain
 * @date 2018/5/23 14:08
 */

@Component( "myAuthenticationFailHandle" )
public class MyAuthenticationFailHandle extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse httpServletResponse, AuthenticationException exception)
            throws IOException, ServletException {

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.write(CharsetUtil.AUTHENTICATION_FAIL);
        out.flush();
        out.close();
    }

    @Override
    protected boolean isAllowSessionCreation() {
        return false;
    }
}
