package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.CarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/7/28 8:36
 */
@Repository
public interface CarInfoRepository  extends JpaRepository<CarInfo,String>{

    CarInfo findByModel(String model);

    List<CarInfo> findByUpdatetimeBetween(Long startTime, Long endTime);

    Long countByDisabled(boolean disabled);

    Long countByPerfectState(boolean perfectState);

}
