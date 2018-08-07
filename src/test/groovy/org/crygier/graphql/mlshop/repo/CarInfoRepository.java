package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.CarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Curtain
 * @date 2018/7/28 8:36
 */
@Repository
public interface CarInfoRepository  extends JpaRepository<CarInfo,String>{

    CarInfo findByModel(String model);
}
