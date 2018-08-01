package org.crygier.graphql.mlshop.service.impl;

import org.crygier.graphql.mlshop.model.ConcernCar;
import org.crygier.graphql.mlshop.repo.ConcernCarRepository;
import org.crygier.graphql.mlshop.service.ConcernCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2018/8/1 15:38
 */

@Service
public class ConcernCarServiceImpl implements ConcernCarService {

    @Autowired
    private ConcernCarRepository concernCarRepository;

    @Override
    public ConcernCar concern(ConcernCar concernCar) {
        return concernCarRepository.save(concernCar);
    }

    @Override
    public void cancel(String id) {
        concernCarRepository.deleteById(id);
    }
}
