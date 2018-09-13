package org.crygier.graphql.mlshop.config;


import org.crygier.graphql.mlshop.authclient.ClientAuthenticationFilter;
import org.crygier.graphql.mlshop.authclient.ClientAuthenticationProvider;
import org.crygier.graphql.mlshop.service.AdministService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@CrossOrigin(origins = {}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyRestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private AdministService administService;

    @Autowired
    private MyAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailHandle myAuthenticationFailHandle;

    @Autowired
    private MyAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private MySimpleLogoutHandler mySimpleLogoutHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.headers().frameOptions().sameOrigin();

        AuthenticationManager am = this.authenticationManager();

        http.cors().and().
                csrf().disable()
                .addFilterBefore(ssoFilter(am), BasicAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/login","/client/**","/mlshop/getcode","/mlshop/verify","/registeruser","/forgetpassword","/sts","/mlshop/pay","/mlshop/notify","/graphql").permitAll()
                .antMatchers("/**").authenticated()
                // .antMatchers("/agency/**").hasAnyRole("ADMIN")
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(myAuthenticationFailHandle)
                .and()
                .logout().logoutSuccessHandler(mySimpleLogoutHandler);

        /*避免用户多地登录*/
        http.sessionManagement().maximumSessions(1);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/graphql/**");
    }

    @Bean
    public MyAuthenticationSuccessHandler mySuccessHandler() {
        return new MyAuthenticationSuccessHandler();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.administService)
                .passwordEncoder(new MyPasswordEncoder());
         auth.authenticationProvider(new ClientAuthenticationProvider());
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.addAllowedHeader("x-auth-token");
        configuration.addExposedHeader("x-auth-token");
        configuration.addAllowedHeader("content-type");

        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private Filter ssoFilter(AuthenticationManager am) {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        ClientAuthenticationFilter wmaFilter = new ClientAuthenticationFilter();
        wmaFilter.setAuthenticationManager(am);
        wmaFilter.setAuthenticationSuccessHandler(new MyAuthenticationSuccessHandler(false));
        wmaFilter.setAuthenticationFailureHandler(this.myAuthenticationFailHandle);
        filters.add(wmaFilter);
        filter.setFilters(filters);

        /*facebok，github的第三方登录
        filters.add(ssoFilter(facebook(), "/login/facebook"));
        filters.add(ssoFilter(github(), "/login/github"));
        */

        return filter;
    }
}
