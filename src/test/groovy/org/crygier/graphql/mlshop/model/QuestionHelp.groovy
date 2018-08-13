package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.BosEntity
import cn.wzvtcsoft.x.bos.domain.Bostype
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2018/8/13 16:52
 */

@Entity
@SchemaDocumentation("问答帮助中心")
@CompileStatic
@Bostype("A21")
public class QuestionHelp extends BosEntity{

    @SchemaDocumentation("问题")
    String question;

    @SchemaDocumentation("回答")
    String answer;

}
