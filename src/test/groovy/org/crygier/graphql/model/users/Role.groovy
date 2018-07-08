package org.crygier.graphql.model.users

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@SchemaDocumentation("UserRoleItem who uses the application")
@CompileStatic
@Table(name="roles")
public class Role {
	@Id
	@SchemaDocumentation("Primary Key for the UserRoleItem Class")
	String id;

	String name;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "parent",orphanRemoval = true)
	Set<RolePrevilegeItem> previlegeItems;
}
