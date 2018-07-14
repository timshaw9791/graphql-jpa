package org.crygier.graphql.model.users

import cn.wzvtcsoft.x.bos.domain.BaseEntity
import org.crygier.graphql.annotation.GraphQLIgnore

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id

import org.crygier.graphql.annotation.SchemaDocumentation

import groovy.transform.CompileStatic

import javax.persistence.OneToMany

@Entity
@SchemaDocumentation("User who uses the application")
@CompileStatic
class User{

	@Id
	@SchemaDocumentation("Primary Key for the User Class")
	String id;


	String firstName;
	public String getId(){
		return id;
	}
	
	String lastName;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "parent",orphanRemoval = true)
	Set<UserRoleItem> roleItems;
}
