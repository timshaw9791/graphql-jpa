package org.crygier.graphql.model.users

import cn.wzvtcsoft.x.bos.domain.Entry
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("RolePrevilegeItem who uses the application")
@CompileStatic
public class RolePrevilegeItem extends Entry{

	@ManyToOne(fetch =FetchType.LAZY)
	Privi privi;
}
