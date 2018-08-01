package org.crygier.graphql.mlshop.service.impl;

import cn.wzvtcsoft.x.bos.domain.util.BeanCopyUtil;
import org.crygier.graphql.mlshop.model.CarInfo;
import org.crygier.graphql.mlshop.model.FinancialScheme;
import org.crygier.graphql.mlshop.repo.CarInfoRepository;
import org.crygier.graphql.mlshop.service.CarInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Curtain
 * @date 2018/7/30 18:25
 */
@Service
public class CarInfoServiceImpl implements CarInfoService {

    @Autowired
    private CarInfoRepository carInfoRepository;

    @Override
    public CarInfo update(CarInfo carInfo) {
        CarInfo result = findOne(carInfo.getId());
        Set<FinancialScheme> financialSchemesItems = carInfo.getFinancialSchemesItems();
        Set<FinancialScheme> financialSchemesItems1 = result.getFinancialSchemesItems();
        BeanUtils.copyProperties(financialSchemesItems,financialSchemesItems1,BeanCopyUtil.getNullPropertyNames(financialSchemesItems));
        BeanUtils.copyProperties(carInfo, result, BeanCopyUtil.getNullPropertyNames(carInfo));
        result.setFinancialSchemesItems(financialSchemesItems1);
        return carInfoRepository.save(result);
    }

    @Override
    public CarInfo findOne(String id) {
        return carInfoRepository.findById(id).get();
    }

    @Override
    public CarInfo save(CarInfo carInfo) {
        return carInfoRepository.save(carInfo);
    }
}
