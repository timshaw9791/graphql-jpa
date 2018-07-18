package org.crygier.graphql.model.users;


import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.Bostype;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Entity;

@Entity
@SchemaDocumentation("User who uses the application")
@CompileStatic
@Bostype("A03")
public class Department extends BosEntity {


}
