package org.crygier.graphql.mlshop.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("authenticationSuccessHandler")
public class MyAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    MyAuthenticationSuccessHandler(){
        super();
    }

    public MyAuthenticationSuccessHandler(boolean isFromOAuth2) {
        super();
        this.isFromOAuth2 = isFromOAuth2;
    }

    boolean isFromOAuth2=false;

    final String PRESCRIPT="function GetQueryString(name){var reg = new RegExp('(^|&)'+ name +'=([^&]*)(&|$)');var r = window.location.search.substr(1).match(reg);if(r!=null)return unescape(r[2]); return null;}\n"
    +"this.localStorage.setItem('x-auth-token','$token$');window.location.href=GetQueryString('state')";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException {

        SavedRequest savedRequest
                = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            if(isFromOAuth2){

            String sid=request.getSession().getId();
            //返回文本数据
            response.setContentType("application/json;charset=utf-8");
            ServletOutputStream outputStream = response.getOutputStream();
            //修改  直接返回token
            //outputStream.write(("<script>"+PRESCRIPT.replace("$token$", sid)+"</script>").getBytes("utf-8"));
            outputStream.write(sid.getBytes("utf-8"));
            //outputStream.write(password.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();

            }
            return ;
        }
        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl()
                || (targetUrlParam != null
                && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}