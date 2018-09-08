package org.crygier.graphql.wechatpay.service.impl;

import org.crygier.graphql.mlshop.model.Salesman;
import org.crygier.graphql.mlshop.repo.SalesmanRepository;
import org.crygier.graphql.mlshop.service.SalesmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2018/9/4 9:25
 */
@Service
public class SalesmanServiceImpl implements SalesmanService {

    @Autowired
    private SalesmanRepository salesmanRepository;

    @Override
    public Salesman findOne(String id) {
        return salesmanRepository.findById(id).get();
    }
}
