package org.crygier.graphql.mlshop.controller

import org.crygier.graphql.mlshop.bean.BargainSetting
import org.crygier.graphql.mlshop.service.BargainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
/**
 * @author Curtain
 * @date 2018/8/27 8:39
 */
@RestController
@RequestMapping("/bargain")
public class BargainSettingController {

    @Autowired
    private BargainService bargainService;

    /*保存一条砍价设置*/
    @RequestMapping("/savebarginsetting")
    public BargainSetting saveBargainSetting(@RequestBody BargainSetting bargainSetting){
        return bargainService.saveBargainSetting(bargainSetting);
    }

    /*修改一条砍价设置*/
    @RequestMapping("/updatebarginsetting")
    public BargainSetting updateBargainSetting(@RequestBody BargainSetting bargainSetting){
        return bargainService.updateBargainSetting(bargainSetting);
    }

    /*查找一条砍价设置*/
    @RequestMapping("/findbarginsetting")
    public BargainSetting findBargainSetting(){
        return bargainService.findBargainSetting(bargainSetting);
    }

}
