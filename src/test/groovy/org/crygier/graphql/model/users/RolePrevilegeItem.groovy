package org.crygier.graphql.model.users

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("RolePrevilegeItem who uses the application")
@CompileStatic
public class RolePrevilegeItem {
	@Id
	@SchemaDocumentation("Primary Key for the RolePrevilegeItem Class")
	String id;

	@ManyToOne(fetch = FetchType.LAZY)
	public Role parent;

	@ManyToOne(fetch =FetchType.LAZY)
	Privi privi;
}
