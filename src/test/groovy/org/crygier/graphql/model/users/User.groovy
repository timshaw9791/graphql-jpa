package org.crygier.graphql.model.users

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.FieldNullEnum
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
@SchemaDocumentation("User who uses the application")
@CompileStatic
@Bostype("C03")
//默认匿名用户可以访问/默认登陆用户可以访问，默认所有用户禁止访问
//context(x,访问自己的订单)
//@QueryDeny(exceptPredicate=QueryPermission.permission)
//@QueryAllow(exceptPredicate="",exceptAttrs="{}")
class User extends BosEntity{

	String firstName;
	
	String lastName;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "parent",orphanRemoval = true)
	Set<UserRoleItem> roleItems=new HashSet<>();


	FieldNullEnum filedNullEnum;


}


final class QueryPermission //extends QueryPermissionBase
{
    static final String permission="abc(user,data)";
    public boolean abc(User,User Data){
        //if()
    }

}








