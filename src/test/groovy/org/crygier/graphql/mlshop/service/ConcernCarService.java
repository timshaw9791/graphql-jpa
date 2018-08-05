package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.ConcernCar;

/**
 * @author Curtain
 * @date 2018/8/1 15:32
 */
public interface ConcernCarService {

    /**
     * 关注汽车
     * @param concernCar
     * @return
     */
    ConcernCar concern(ConcernCar concernCar);

    /**
     * 取消关注
     * @param id
     * @return
     */
    void cancel(String id);
}
