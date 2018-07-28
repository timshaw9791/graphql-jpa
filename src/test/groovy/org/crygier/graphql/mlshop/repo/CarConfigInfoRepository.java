package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.CarConfigInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Curtain
 * @date 2018/7/28 8:30
 */
@Repository
public interface CarConfigInfoRepository extends JpaRepository<CarConfigInfo,String> {
}
