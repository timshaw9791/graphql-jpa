package org.crygier.graphql.model.users

import cn.wzvtcsoft.x.bos.domain.BosEntity
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@SchemaDocumentation("UserRoleItem who uses the application")
@CompileStatic
@Table(name="roles")
public class Role extends BosEntity{

	String name;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "parent",orphanRemoval = true)
	Set<RolePrevilegeItem> previlegeItems=null;
}
