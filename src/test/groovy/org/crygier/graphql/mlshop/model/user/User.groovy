package org.crygier.graphql.mlshop.model.user

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.Customer

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
@SchemaDocumentation("用户")
@CompileStatic
@Bostype("C03")
//默认匿名用户可以访问/默认登陆用户可以访问，默认所有用户禁止访问
//context(x,访问自己的订单)
//@QueryDeny(exceptPredicate=QueryPermission.permission)
//@QueryAllow(exceptPredicate="",exceptAttrs="{}")
class User extends BosEntity{

	@SchemaDocumentation("用户名")
	private String username;

	@SchemaDocumentation("密码")
	private String password;

	@SchemaDocumentation("注册手机号")
	private String registerPhone;

	@SchemaDocumentation("在用手机号")
	private String phone;

	@SchemaDocumentation("客户信息")
	@ManyToOne
	Customer customer;

	@SchemaDocumentation("头像")
	private String headImg;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "parent",orphanRemoval = true)
	Set<UserRoleItem> roleItems=new HashSet<>();
}


//final class QueryPermission //extends QueryPermissionBase
//{
//    static final String permission="abc(user,data)";
//    public boolean abc(User,User Data){
//        //if()
//    }
//
//}








