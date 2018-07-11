package org.crygier.graphql.model.users

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("UserRoleItem who uses the application")
@CompileStatic
public class PriviGroup {
	@Id
	@SchemaDocumentation("Primary Key for the UserRoleItem Class")
	String id;

	String name;

	@ManyToOne
	Privi privi
}
