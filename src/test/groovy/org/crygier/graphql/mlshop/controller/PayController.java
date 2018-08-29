package org.crygier.graphql.mlshop.controller;

import org.crygier.graphql.mlshop.bean.WxPaySyncResponse;
import org.crygier.graphql.mlshop.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Curtain
 * @date 2018/8/29 11:07
 */

@Controller
@RequestMapping("/mlshop")
@CrossOrigin(origins = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS},maxAge=1800L,allowedHeaders ="*")
public class PayController {
    @Autowired
    private PayService payService;

    @RequestMapping("/pay")
    public String weChatPay(){
        WxPaySyncResponse response = payService.weChatPay();
        String url = response.getMwebUrl();
        return "redirect:"+url;

    }

    @PostMapping("/notify")
    private void notify(@RequestBody String notifyData) {
        System.out.println(notifyData);
    }

}
