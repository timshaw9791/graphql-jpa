package org.crygier.graphql.mlshop.service.impl;

import cn.wzvtcsoft.x.bos.domain.util.BeanCopyUtil;
import org.crygier.graphql.mlshop.model.CarInfo;
import org.crygier.graphql.mlshop.model.FinancialScheme;
import org.crygier.graphql.mlshop.repo.CarInfoRepository;
import org.crygier.graphql.mlshop.service.CarInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        if (!(carInfo.getModel().equals(result.getModel()))) {
            CarInfo model = carInfoRepository.findByModel(carInfo.getModel());
            if (model != null) {
                throw new RuntimeException("The modification failed, the model already existed");
//
//            if (carInfo.getFinancialSchemesItems() != null && carInfo.getFinancialSchemesItems().size()>0) {
//                model.getFinancialSchemesItems().clear();
//                model.getFinancialSchemesItems().addAll(carInfo.getFinancialSchemesItems());
//                carInfo.setFinancialSchemesItems(null);
//            }
//            BeanUtils.copyProperties(carInfo, model, BeanCopyUtil.getNullPropertyNames(carInfo));
//
//            return carInfoRepository.save(model);
            }
        }

        return carInfoRepository.save(carInfo);
    }

    @Override
    public void deleteById(String id) {
        CarInfo carInfo = findOne(id);
        carInfo.setDisabled(true);
        carInfoRepository.save(carInfo);

    }

    @Override
    public CarInfo findOne(String id) {
        return carInfoRepository.findById(id).get();
    }

    @Override
    public CarInfo save(CarInfo carInfo) {
        CarInfo model = carInfoRepository.findByModel(carInfo.getModel());
        if (model != null) {
            model.setGuidePrice(carInfo.getGuidePrice());
            model.setBrand(carInfo.getBrand());
            model.setFilename(carInfo.getFilename());
            return carInfoRepository.save(model);
        }

        return carInfoRepository.save(carInfo);
    }
}
