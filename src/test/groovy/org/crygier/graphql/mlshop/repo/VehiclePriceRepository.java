package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.VehiclePrice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Curtain
 * @date 2018/8/13 15:40
 */
public interface VehiclePriceRepository extends JpaRepository<VehiclePrice,String> {
}
