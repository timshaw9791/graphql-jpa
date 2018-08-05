package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.ConcernCar;
import org.crygier.graphql.mlshop.model.ConcernShop;

/**
 * @author Curtain
 * @date 2018/8/1 15:32
 */
public interface ConcernShopService {

    /**
     * 关注门店
     * @param concernShop
     * @return
     */
    ConcernShop concern(ConcernShop concernShop);

    /**
     * 取消关注
     * @param id
     * @return
     */
    void cancel(String id);
}
