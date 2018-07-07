package org.crygier.graphql.model.users

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("UserRoleItem who uses the application")
@CompileStatic
public class UserRoleItem {
	@Id
	@SchemaDocumentation("Primary Key for the UserRoleItem Class")
	String id;

	@ManyToOne(fetch = FetchType.LAZY)
	public User parent;

	@ManyToOne(fetch =FetchType.LAZY)
	Role role;
}
