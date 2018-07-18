package org.crygier.graphql.model.users

import cn.wzvtcsoft.x.bos.domain.BosEntity
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity

@Entity
@SchemaDocumentation("UserRoleItem who uses the application")
@CompileStatic
public class Privi extends BosEntity{

	String name;
}
