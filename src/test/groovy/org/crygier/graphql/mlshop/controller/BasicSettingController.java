package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.crygier.graphql.mlshop.model.QuestionHelp;
import org.crygier.graphql.mlshop.repo.QuestionHelpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Curtain
 * @date 2018/8/13 17:11
 */

@SchemaDocumentation("基础信息")
@GRestController("mlshop")
@RestController
@CompileStatic
public class BasicSettingController {

    @Autowired
    private QuestionHelpRepository questionHelpRepository;

    @SchemaDocumentation("添加问答帮助信息")
    @GRequestMapping(path = "/addquestionhelp", method = RequestMethod.POST)
    QuestionHelp addQuestionHelp(@RequestParam(name = "questionhelp", required = true) QuestionHelp questionHelp) {
        return this.questionHelpRepository.save(questionHelp);
    }

    @SchemaDocumentation("修改问答帮助信息")
    @GRequestMapping(path = "/updatequestionhelp", method = RequestMethod.POST)
    QuestionHelp updateQuestionHelp(@RequestParam(name = "questionhelp", required = true) QuestionHelp questionHelp) {
        return this.questionHelpRepository.save(questionHelp);
    }

    @SchemaDocumentation("删除问答帮助信息")
    @GRequestMapping(path = "/removequestionhelp", method = RequestMethod.POST)
    QuestionHelp removeQuestionHelp(@RequestParam(name = "questionhelp", required = true) QuestionHelp questionHelp) {
        questionHelpRepository.deleteById(questionHelp.getId());
        return questionHelp;
    }
}
