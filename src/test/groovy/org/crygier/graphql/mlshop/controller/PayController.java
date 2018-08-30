package org.crygier.graphql.mlshop.controller;

import org.crygier.graphql.mlshop.service.PayService;
import org.crygier.graphql.wechatpay.model.response.PayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Curtain
 * @date 2018/8/29 11:07
 */

@RestController
@RequestMapping("/mlshop")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, maxAge = 1800L, allowedHeaders = "*")
public class PayController {
    @Autowired
    private PayService payService;

    @RequestMapping("/pay")
    public String weChatPay(@RequestParam(name = "orderid") String orderId, @RequestParam(name = "ip") String ip) {
        PayResponse payResponse = payService.weChatPay(orderId, ip);
        return payResponse.getMwebUrl();

    }

    @PostMapping("/notify")
    private void notify(@RequestBody String notifyData) {
        System.out.println(notifyData);
    }

}
