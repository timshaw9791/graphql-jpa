package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.CarInfo;
import org.crygier.graphql.mlshop.repo.CarInfoRepository;
import org.crygier.graphql.mlshop.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Curtain
 * @date 2018/7/30 18:25
 */
public class CarInfoServiceImpl implements CarInfoService {

    @Autowired
    private CarInfoRepository carInfoRepository;

    @Override
    public CarInfo update(CarInfo carInfo) {

        return carInfoRepository.save(carInfo);
    }
}
