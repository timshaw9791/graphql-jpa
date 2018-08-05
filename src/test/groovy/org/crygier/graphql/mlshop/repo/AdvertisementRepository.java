package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Curtain
 * @date 2018/8/2 10:32
 */
public interface AdvertisementRepository extends JpaRepository<Advertisement,String> {
}
