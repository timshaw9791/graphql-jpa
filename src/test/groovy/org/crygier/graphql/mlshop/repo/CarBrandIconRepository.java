package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.CarBrandIcon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/8/14 9:57
 */
public interface CarBrandIconRepository extends JpaRepository<CarBrandIcon,String> {

    Optional<CarBrandIcon> findByBrand(String brand);
}
