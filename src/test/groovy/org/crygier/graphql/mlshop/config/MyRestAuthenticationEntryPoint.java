package org.crygier.graphql.mlshop.config;


import org.crygier.graphql.mlshop.util.CharsetUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component( "restAuthenticationEntryPoint" )
public class MyRestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,CharsetUtil.AUTHENTICATION_FAIL);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(CharsetUtil.UN_LOGIN);
        out.flush();
        out.close();

    }
}
