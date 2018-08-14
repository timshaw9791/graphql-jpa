package org.crygier.graphql.mlshop.controller;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.annotation.SchemaDocumentation;
import org.crygier.graphql.mlshop.model.Advertisement;
import org.crygier.graphql.mlshop.model.Feedback;
import org.crygier.graphql.mlshop.model.QuestionHelp;
import org.crygier.graphql.mlshop.repo.AdvertisementRepository;
import org.crygier.graphql.mlshop.repo.FeedbackRepository;
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

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    AdvertisementRepository advertisementRepository;

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

    @SchemaDocumentation("添加反馈")
    @GRequestMapping(path = "/addfeedback", method = RequestMethod.POST)
    Feedback addFeedback(@RequestParam(name = "feedback", required = true) Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @SchemaDocumentation("删除反馈")
    @GRequestMapping(path = "/removefeedback", method = RequestMethod.POST)
    Feedback removeFeedback(@RequestParam(name = "feedback", required = true) Feedback feedback) {
        feedbackRepository.deleteById(feedback.getId());
        return feedback;
    }

    @SchemaDocumentation("/添加广告")
    @GRequestMapping(path = "/addadvertisement",method = RequestMethod.POST)
    Advertisement addAdvertisement(@RequestParam(name = "advertisement",required = true) Advertisement advertisement){
        return advertisementRepository.save(advertisement);
    }

    @SchemaDocumentation("/修改广告")
    @GRequestMapping(path = "/updateadvertisement",method = RequestMethod.POST)
    Advertisement updateAdvertisement(@RequestParam(name = "advertisement",required = true) Advertisement advertisement){
        return advertisementRepository.save(advertisement);
    }

    @SchemaDocumentation("/删除广告")
    @GRequestMapping(path = "/removeadvertisement",method = RequestMethod.POST)
    Advertisement removeAdvertisement(@RequestParam(name = "advertisement",required = true) Advertisement advertisement){
        advertisementRepository.deleteById(advertisement.getId());
        return advertisement;
    }

}
