package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.CarSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  CarSourceRepository extends JpaRepository<CarSource,String> {
}
