package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import javax.persistence.Entity

@Entity
@SchemaDocumentation("管理员")
@CompileStatic
@Bostype("A06")
public class Administ extends BosEntity implements UserDetails{
    @SchemaDocumentation("姓名")
    String name;

    @SchemaDocumentation("联系方式")
    String tel;

    @SchemaDocumentation("用户名")
    String username;

    @SchemaDocumentation("密码")
    String password;

    @SchemaDocumentation("出生年月")
    String birthday;

    @SchemaDocumentation("等级")
    String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list=new ArrayList<>();
//        for(String role : roleAuthority.split(",")){
//            list.add(new SimpleGrantedAuthority(role));
//        }
        return list;
    }

    @Override
    String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return this.username;
    }

}
