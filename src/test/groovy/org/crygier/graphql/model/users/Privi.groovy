package org.crygier.graphql.model.users

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
@SchemaDocumentation("UserRoleItem who uses the application")
@CompileStatic
public class Privi {
	@Id
	@SchemaDocumentation("Primary Key for the UserRoleItem Class")
	String id;

	String name;
}
