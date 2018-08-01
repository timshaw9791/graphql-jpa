package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.ConcernShop;
import org.crygier.graphql.mlshop.repo.ConcernShopRepository;
import org.crygier.graphql.mlshop.service.ConcernShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2018/8/1 15:41
 */

@Service
public class ConcernShopServiceImpl implements ConcernShopService {

    @Autowired
    private ConcernShopRepository concernShopRepository;

    @Override
    public ConcernShop concern(ConcernShop concernShop) {
        return concernShopRepository.save(concernShop);
    }

    @Override
    public void cancel(String id) {
        concernShopRepository.deleteById(id);
    }
}
