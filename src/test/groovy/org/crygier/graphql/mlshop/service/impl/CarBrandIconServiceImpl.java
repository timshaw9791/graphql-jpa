package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.CarBrandIcon;
import org.crygier.graphql.mlshop.repo.CarBrandIconRepository;
import org.crygier.graphql.mlshop.service.CarBrandIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/8/14 10:02
 */
@Service
public class CarBrandIconServiceImpl implements CarBrandIconService {

    @Autowired
    private CarBrandIconRepository carBrandIconRepository;

    @Override
    public CarBrandIcon save(CarBrandIcon carBrandIcon) {
        Optional<CarBrandIcon> optional = carBrandIconRepository.findByBrand(carBrandIcon.getBrand());
        if (optional.isPresent()){
            CarBrandIcon result = optional.get();
            result.setIcon(carBrandIcon.getIcon());
            return carBrandIconRepository.save(result);
        }
        return carBrandIconRepository.save(carBrandIcon);
    }

    @Override
    public void saveAll(Collection<CarBrandIcon> carBrandIcons) {
        carBrandIconRepository.saveAll(carBrandIcons);
    }
}
