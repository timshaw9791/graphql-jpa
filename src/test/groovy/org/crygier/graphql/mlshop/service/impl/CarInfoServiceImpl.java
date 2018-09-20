package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.exception.MLShopRunTimeException;
import org.crygier.graphql.mlshop.model.CarInfo;
import org.crygier.graphql.mlshop.repo.CarInfoRepository;
import org.crygier.graphql.mlshop.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                throw new RuntimeException("修改失败，这个型号的车已经存在");
//
//            if (carInfo.getFinancialSchemesItems() != null && carInfo.getFinancialSchemesItems().size()>0) {
//                model.getFinancialSchemesItems().clear();
//                model.getFinancialSchemesItems().addAll(carInfo.getFinancialSchemesItems());
//                carInfo.setFinancialSchemesItems(null);
//            }
//            BeanUtils.copyProperties(carInfo, model, BeanCopyUtil.getNullPropertyNames(carInfo));
//
//            return carInfoRepository.saveBargainSetting(model);
            }
        }

//        //表示已完善车辆信息
//        carInfo.setPerfectState(true);

        return carInfoRepository.save(carInfo);
    }

    @Override
    public void enableCarInfo(String id) {
        CarInfo carInfo = findOne(id);
        carInfo.setDisabled(false);
        carInfoRepository.save(carInfo);
    }

    @Override
    public List<CarInfo> findByUpdateTime(Long startTime, Long endTime) {
        return carInfoRepository.findByUpdatetimeBetween(startTime,endTime);
    }

    @Override
    public void deleteById(String id) {
        CarInfo carInfo = findOne(id);
        carInfo.setDisabled(true);
        carInfoRepository.save(carInfo);

    }

    @Override
    public CarInfo findOne(String id) {
        Optional<CarInfo> optional = carInfoRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }
        throw new MLShopRunTimeException("未找到这辆车的信息");
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
