package org.crygier.graphql.mlshop.controller;

import org.crygier.graphql.mlshop.service.PayService;
import org.crygier.graphql.wechatpay.model.response.PayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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
    public String weChatPay(@RequestParam(name = "orderid") String orderId,HttpServletRequest httpServletRequest) {
        PayResponse payResponse = payService.weChatPay(orderId,httpServletRequest);
        
        return payResponse.getMwebUrl();

    }

    @PostMapping("/notify")
    private ModelAndView notify(@RequestBody String notifyData) {
       payService.notify(notifyData);
        //返回微信处理结果
        return new ModelAndView("templates/pay/success");
    }

    @PostMapping("/refund")
    private void refund(@RequestParam("orderid") String orderId){
        payService.refund(orderId);
    }
}
