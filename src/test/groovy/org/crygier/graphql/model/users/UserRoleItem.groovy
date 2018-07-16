package org.crygier.graphql.model.users

import cn.wzvtcsoft.x.bos.domain.Bostype
import cn.wzvtcsoft.x.bos.domain.Entry
import com.fasterxml.jackson.annotation.JsonBackReference
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@SchemaDocumentation("UserRoleItem who uses the application")
@CompileStatic
@Bostype("B03")
public class UserRoleItem extends Entry{

	@ManyToOne(fetch =FetchType.LAZY)
	Role role;
}
