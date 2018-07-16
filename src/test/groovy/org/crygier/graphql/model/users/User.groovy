package org.crygier.graphql.model.users

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
@SchemaDocumentation("User who uses the application")
@CompileStatic
@Bostype("C03")
class User extends BosEntity{



	String firstName;
	
	String lastName;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "parent",orphanRemoval = true)
	Set<UserRoleItem> roleItems;


}
