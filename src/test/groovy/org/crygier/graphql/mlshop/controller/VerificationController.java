package org.crygier.graphql.mlshop.controller;

import org.crygier.graphql.mlshop.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Curtain
 * @date 2018/8/9 8:38
 */

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, maxAge = 1800L, allowedHeaders = "*")
@RequestMapping("/mlshop")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @RequestMapping("/getcode")
    public Object getCode(@RequestParam("phone") String phone,@RequestParam("type") Integer type){
       return verificationService.getCode(phone,type);
    }

    @RequestMapping("/verify")
    public Object verify(@RequestParam("phone") String phone,@RequestParam("code") String code,@RequestParam("type") Integer type){
        return verificationService.verify(code,phone,type);
    }


}
