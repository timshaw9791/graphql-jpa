package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.CarBrandIcon;

import java.util.Collection;

/**
 * @author Curtain
 * @date 2018/8/14 9:58
 */
public interface CarBrandIconService {

    /**
     * 保存一条信息 如果存在则覆盖
     * @param carBrandIcon
     * @return
     */
    CarBrandIcon save(CarBrandIcon carBrandIcon);

    /**
     * 保存所有
     * @param carBrandIcons
     */
    void saveAll(Collection<CarBrandIcon> carBrandIcons);
}
