package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.CarConfigInfo;
import org.crygier.graphql.mlshop.repo.CarConfigInfoRepository;
import org.crygier.graphql.mlshop.service.CarConfigInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2018/7/28 14:06
 */
@Service
public class CarConfigInfoServiceImpl implements CarConfigInfoService {

    @Autowired
    private CarConfigInfoRepository carConfigInfoRepository;


    @Override
    public CarConfigInfo save(CarConfigInfo carConfigInfo) {
        CarConfigInfo result = carConfigInfoRepository.findByBrand(carConfigInfo.getBrand());
        if (result!=null){
            result.setFilename(carConfigInfo.getFilename());
            result.setGuidePrice(carConfigInfo.getGuidePrice());
            result.setModel(carConfigInfo.getModel());
            return carConfigInfoRepository.save(result);
        }
        return carConfigInfoRepository.save(carConfigInfo);
    }
}
